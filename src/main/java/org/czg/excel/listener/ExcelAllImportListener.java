package org.czg.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.czg.excel.exception.BizException;
import org.czg.excel.model.ExcelLineResult;
import org.czg.excel.util.ValidationUtils;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 导入的数据一次性处理
 *
 * @author czg
 */
@RequiredArgsConstructor
@Slf4j
public class ExcelAllImportListener<T> extends AnalysisEventListener<T> {

    /**
     * 校验返回数据
     */
    @Getter
    private final List<ExcelLineResult<T>> resultList = new ArrayList<>();

    /**
     * 解析接收数据
     */
    private final List<T> dataList = new ArrayList<>();

    private static final String BIZ_ERROR_MSG = "系统异常";

    /**
     * 业务处理
     */
    private final Consumer<List<T>> consumer;
 
    /**
     * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        if (log.isDebugEnabled()) {
            log.debug("读取数据：{}", data);
        }
        dataList.add(data);
        // 数据存储到list
        ExcelLineResult<T> result = ExcelLineResult.<T>builder()
                .rowIndex(context.readRowHolder().getRowIndex())
                .data(data)
                .build();
        resultList.add(result);
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
        });
        boolean allSuccess = resultList.stream()
                .allMatch(it -> it.getViolations().isEmpty() && Objects.isNull(it.getBizErrorMsg()));
        if (allSuccess) {
            try {
                consumer.accept(dataList);
            } catch (BizException e) {
                throw new BizException(e.getMessage());
            } catch (Exception e) {
                throw new BizException(BIZ_ERROR_MSG);
            }
        }
    }

}