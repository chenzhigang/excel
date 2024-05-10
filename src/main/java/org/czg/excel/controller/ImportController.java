package org.czg.excel.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.czg.excel.model.Result;
import org.czg.excel.model.UserExcelModel;
import org.czg.excel.service.ImportService;
import org.czg.excel.util.ExcelUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author czg
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/import")
public class ImportController {

    private final ImportService<List<UserExcelModel>> allImportService;

    private final ImportService<UserExcelModel> importService;

    @PostMapping("readAll")
    public Result<Void> allImportExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception {
        ExcelUtil.readAll(file, UserExcelModel.class, allImportService, response);
        return Result.success();
    }

    @PostMapping("read")
    public Result<Void> importExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception {
        ExcelUtil.read(file, UserExcelModel.class, true, importService, response);
        return Result.success();
    }


}
