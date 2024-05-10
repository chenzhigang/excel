package org.czg.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.czg.excel.exception.BizException;
import org.czg.excel.model.ExcelLineResult;
import org.czg.excel.util.ValidationUtils;

import javax.validation.ConstraintViolation;
import java.util.*;
import java.util.function.Consumer;

/**
 * 导入的数据一条一条处理
 *
 * @author czg
 */
@RequiredArgsConstructor
@Slf4j
public class ExcelImportListener<T> extends AnalysisEventListener<T> {

    /**
     * 校验返回数据
     */
    @Getter
    private final List<ExcelLineResult<T>> resultList = new ArrayList<>();

    private static final String BIZ_ERROR_MSG = "系统异常";

    /**
     * 业务处理
     */
    private final Consumer<T> consumer;

    /**
     * 表头
     */
    private final Map<Integer, String> importHeadMap;
 
    /**
     * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        if (log.isDebugEnabled()) {
            log.debug("读取数据：{}", data);
        }
        // 数据存储到list
        ExcelLineResult<T> result = ExcelLineResult.<T>builder()
                .rowIndex(context.readRowHolder().getRowIndex())
                .data(data)
                .build();
        resultList.add(result);
    }

    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
        if (headMap.isEmpty()) {
            throw new BizException("导入失败，模板不对，表头不存在");
        }
        if (null != importHeadMap && !importHeadMap.isEmpty()) {
            if (headMap.size() != importHeadMap.size()) {
                throw new BizException("导入失败，模板不对，表头数量不一致");
            }
            // 校验表头是否符合
            for (Map.Entry<Integer, String> entry : importHeadMap.entrySet()) {
                CellData cellData = headMap.get(entry.getKey());
                if (null == cellData) {
                    throw new BizException(String.format("导入失败，模板不对，传入的表头与接收的表头[%s]位置不匹配", entry.getValue()));
                }
                if (!entry.getValue().equals(cellData.toString().trim())) {
                    throw new BizException(String.format("导入失败，模板不对，第%s列表头[%s]不存在", (entry.getKey() + 1), entry.getValue()));
                }
            }
        }
        super.invokeHead(headMap, context);
    }

    /**
     * 所有的数据解析完成调用
     *
     * @param context 解析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("数据解析完成");
        if (CollectionUtils.isEmpty(resultList)) {
            log.info("导入的文件数据行为空");
            return;
        }
        resultList.forEach(it -> {
            Set<ConstraintViolation<T>> validate = ValidationUtils.getValidator().validate(it.getData());
            it.setViolations(validate);
            // 校验失败，不执行业务
            if (CollectionUtils.isNotEmpty(validate)) {
                return;
            }
            try {
                consumer.accept(it.getData());
            } catch (BizException e) {
                log.error("[{}]业务处理异常：{}", it, e.getMessage());
                it.setBizErrorMsg(e.getMessage());
            } catch (Exception e) {
                log.error("[{}]解析数据异常", it, e);
                it.setBizErrorMsg(BIZ_ERROR_MSG);
            }
        });

    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        if (exception instanceof BizException) {
            BizException bizException = (BizException) exception;
            super.onException(bizException, context);
        } else {
            super.onException(exception, context);
        }
    }
}