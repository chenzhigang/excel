package org.czg.excel.handler;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.czg.excel.model.ExcelLineResult;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author czg
 */
@Slf4j
@RequiredArgsConstructor
public class ExcelErrorFillHandler<T> implements SheetWriteHandler, RowWriteHandler {
    /**
     * 错误结果集
     */
    private final List<ExcelLineResult<T>> resultList;

    /**
     * 标题所在行, 从1开始
     */
    private final Integer titleLineNumber;

    /**
     * 结果列序号
     */
    private int resultColNum;

    /**
     * 默认导入成功的提示
     */
    private static final String SUCCESS_MSG = "";

    private static final String TITLE_NAME = "失败原因";

    private static void setCellStyle(Cell cell, IndexedColors color) {
        Workbook workbook = cell.getSheet().getWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(color.getIndex());
        style.setFont(font);
        cell.setCellStyle(style);
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet cachedSheet = writeSheetHolder.getCachedSheet();
        for (int i = 1; i <= cachedSheet.getLastRowNum() + 1; i++) {
            // 空白数据, 不做处理
            if (i < titleLineNumber) {
                continue;
            }
            Row row = cachedSheet.getRow(i - 1);
            // 标题行, 创建标题
            if (i == titleLineNumber) {
                this.resultColNum = row.getLastCellNum();
                Cell cell = row.createCell(row.getLastCellNum(), CellType.STRING);
                setCellStyle(cell, IndexedColors.BLACK);
                cell.setCellValue(TITLE_NAME);
                continue;
            }
            // 结果行
            Cell cell = row.createCell(this.resultColNum, CellType.STRING);
            String errMsg = convertErrMsg(resultList.get(i - titleLineNumber - 1));
            if (errMsg == null) {
                setCellStyle(cell, IndexedColors.GREEN);
                cell.setCellValue(SUCCESS_MSG);
                continue;
            }
            setCellStyle(cell, IndexedColors.RED);
            cell.setCellValue(errMsg);
        }
    }

    /**
     * 解析每行的错误信息
     *
     * @param result 读取结果
     * @return 错误信息
     */
    private String convertErrMsg(ExcelLineResult<T> result) {
        if (result.getBizErrorMsg() != null) {
            return result.getBizErrorMsg();
        }
        if (result.getViolations().isEmpty()) {
            return null;
        }
        return result.getViolations().stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(";\r\n"));
    }

    @Override
    public void beforeRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Integer integer, Integer integer1, Boolean aBoolean) {

    }

    @Override
    public void afterRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer integer, Boolean aBoolean) {

    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer integer, Boolean aBoolean) {

    }
}
