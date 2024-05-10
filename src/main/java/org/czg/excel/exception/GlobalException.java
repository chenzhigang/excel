package org.czg.excel.exception;

import lombok.extern.slf4j.Slf4j;
import org.czg.excel.enums.ResultCodeEnum;
import org.czg.excel.model.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author czg
 */
@ControllerAdvice
@Slf4j
public class GlobalException {

    @ResponseBody
    @ExceptionHandler(BizException.class)
    public Result<Object> bizException(BizException exception) {
        return Result.fail(ResultCodeEnum.BIZ_ERROR.getCode(), exception.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result<Object> exception(Exception exception) {
        return Result.fail(ResultCodeEnum.SERVER_ERROR);
    }
}
