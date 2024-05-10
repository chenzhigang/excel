package org.czg.excel.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.exception.ExcelAnalysisException;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.czg.excel.exception.BizException;
import org.czg.excel.handler.ExcelErrorFillHandler;
import org.czg.excel.listener.ExcelAllImportListener;
import org.czg.excel.listener.ExcelImportListener;
import org.czg.excel.model.ExcelLineResult;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * excel工具
 *
 * @author czg
 */
@Slf4j
public class ExcelUtil {

    private static final String SUFFIX_XLSX = ".xlsx";

    private static final String SUFFIX_XLS = ".xls";

    private static final String ERROR_FILE_NAME = "文件导入失败.xlsx";

    /**
     * 导入, 标题行默认为1
     *
     * @param file      文件
     * @param pojoClass 实体类
     * @param consumer  消费数据, 执行SQL逻辑或其他逻辑等等,
     *                  如果抛出BizException异常, 则异常message将作为Excel导入失败原因
     *                  否则为未知异常导致导入失败
     * @param <T>       对应类型
     */
    public static <T> void read(@NotNull MultipartFile file, @NotNull Class<T> pojoClass, @NotNull Consumer<T> consumer, HttpServletResponse response) {
        read(file, pojoClass, false, consumer, 1, response);
    }

    public static <T> void read(@NotNull MultipartFile file, @NotNull Class<T> pojoClass, boolean validHead, @NotNull Consumer<T> consumer, HttpServletResponse response) {
        read(file, pojoClass, validHead, consumer, 1, response);
    }

    /**
     * 导入, 标题行默认为1
     *
     * @param file      文件
     * @param pojoClass 实体类
     * @param consumer  消费数据, 执行SQL逻辑或其他逻辑等等,
     *                  如果抛出BizException异常, 则异常message将作为Excel导入失败原因
     *                  否则为未知异常导致导入失败
     * @param <T>       对应类型
     */
    public static <T> void readAll(@NotNull MultipartFile file, @NotNull Class<T> pojoClass, @NotNull Consumer<List<T>> consumer, HttpServletResponse response) {
        readAll(file, pojoClass, consumer, 1, response);
    }

    /**
     * 导入
     *
     * @param file            文件
     * @param pojoClass       实体类
     * @param consumer        消费数据, 执行SQL逻辑或其他逻辑等等,
     *                        如果抛出BizException异常, 则异常message将作为Excel导入失败原因
     *                        否则为未知异常导致导入失败
     * @param titleLineNumber 标题所在行, 从1开始
     * @param <T>             对应类型
     */
    public static <T> void read(@NotNull MultipartFile file,
                                @NotNull Class<T> pojoClass,
                                boolean validHead,
                                @NotNull Consumer<T> consumer,
                                @NotNull Integer titleLineNumber,
                                HttpServletResponse response) {
        try {
            Map<Integer, String> headMap = new HashMap<>();
            if (validHead) {
                headMap = getHeadMap(pojoClass);
            }
            ExcelImportListener<T> listener = new ExcelImportListener<>(consumer, headMap);
            @Cleanup InputStream inputStream = file.getInputStream();
            EasyExcel.read(inputStream, pojoClass, listener)
                    .headRowNumber(titleLineNumber)
                    .sheet().doRead();
            List<ExcelLineResult<T>> resultList = listener.getResultList();
            boolean allSuccess = resultList.stream()
                    .allMatch(it -> it.getViolations().isEmpty() && Objects.isNull(it.getBizErrorMsg()));
            if (allSuccess) {
                log.info("Excel数据已全部导入成功");
                return;
            }
            @Cleanup InputStream templateIs = file.getInputStream();
            setResponseHeader(response, ERROR_FILE_NAME);

            EasyExcel.write(response.getOutputStream(), pojoClass)
                    .withTemplate(templateIs)
                    .autoCloseStream(false)
                    .registerWriteHandler(new ExcelErrorFillHandler<>(resultList, titleLineNumber))
                    .needHead(false)
                    .sheet()
                    .doWrite(Collections.emptyList());
        } catch (Exception e) {
            log.error("读取文件异常", e);
            if (e instanceof ExcelAnalysisException) {
                String message = e.getCause().getMessage();
                throw new BizException(StringUtils.isEmpty(message) ? "读取文件异常" : message);
            }
        }
    }

    /**
     * 获取类excel注释信息
     *
     * @param pojoClass 类对象
     * @return 类excel注释信息
     * @param <T> 对应类型
     */
    private static <T> Map<Integer, String> getHeadMap(Class<T> pojoClass) {
        Map<Integer, String> resultMap = new HashMap<>();
        List<Field> fieldList = Arrays.asList(pojoClass.getDeclaredFields());
        fieldList.forEach(it -> {
            ExcelProperty declaredAnnotation = it.getDeclaredAnnotation(ExcelProperty.class);
            if (null != declaredAnnotation) {
                String[] value = declaredAnnotation.value();
                int index = declaredAnnotation.index();
                if (null != value && value.length > 0 && index != -1) {
                    resultMap.put(index, value[0].trim());
                }
            }
        });
        return resultMap;
    }

    /**
     * 读取文件后统一处理所有数据
     *
     * @param file 导入文件
     * @param pojoClass 解析接收excel每行数据的类
     * @param consumer 业务处理类
     * @param titleLineNumber 请求表头的行数
     * @param response 返回数据
     * @param <T> 对应类型
     */
    public static <T> void readAll(@NotNull MultipartFile file,
                                   @NotNull Class<T> pojoClass,
                                   @NotNull Consumer<List<T>> consumer,
                                   @NotNull Integer titleLineNumber,
                                   HttpServletResponse response) {
        try {
            ExcelAllImportListener<T> listener = new ExcelAllImportListener<>(consumer);
            @Cleanup InputStream inputStream = file.getInputStream();
            EasyExcel.read(inputStream, pojoClass, listener)
                    .headRowNumber(titleLineNumber)
                    .sheet().doRead();
            List<ExcelLineResult<T>> resultList = listener.getResultList();
            boolean allSuccess = resultList.stream()
                    .allMatch(it -> it.getViolations().isEmpty() && Objects.isNull(it.getBizErrorMsg()));
            if (allSuccess) {
                log.info("Excel数据已全部导入成功");
                return;
            }
            @Cleanup InputStream templateIs = file.getInputStream();
            setResponseHeader(response, ERROR_FILE_NAME);

            EasyExcel.write(response.getOutputStream(), pojoClass)
                    .withTemplate(templateIs)
                    .autoCloseStream(false)
                    .registerWriteHandler(new ExcelErrorFillHandler<>(resultList, titleLineNumber))
                    .needHead(false)
                    .sheet()
                    .doWrite(Collections.emptyList());
        } catch (Exception e) {
            log.error("文件读取失败", e);
            throw new BizException("文件读取失败, 请检查文件格式");
        }
    }

    /**
     * 为下载文件设置响应头
     *
     * @param response 响应
     * @param filename 文件名
     */
    public static void setResponseHeader(HttpServletResponse response, String filename) {
        if (!filename.endsWith(SUFFIX_XLS) && !filename.endsWith(SUFFIX_XLSX)) {
            filename += SUFFIX_XLSX;
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        try {
            filename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replace("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // axios下载时获取文件名
        response.setHeader("filename", filename);
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }
}
