package org.czg.excel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.czg.excel.enums.ResultCodeEnum;

/**
 * @author czg
 */
@SuppressWarnings("unused")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> {

    private String code;

    private String msg;

    private T data;

    public static <T> Result<T> success() {
        return success(ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> success(String code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    public static <T> Result<T> success(T data) {
        return success(ResultCodeEnum.SUCCESS, data);
    }

    public static <T> Result<T> success(ResultCodeEnum codeEnum) {
        return success(codeEnum, null);
    }

    public static <T> Result<T> success(ResultCodeEnum codeEnum, T data) {
        return new Result<>(codeEnum.getCode(), codeEnum.getMsg(), data);
    }

    public static <T> Result<T> fail() {
        return fail(ResultCodeEnum.SERVER_ERROR);
    }

    public static <T> Result<T> fail(String code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    public static <T> Result<T> fail(T data) {
        return success(ResultCodeEnum.SERVER_ERROR, data);
    }

    public static <T> Result<T> fail(ResultCodeEnum codeEnum) {
        return success(codeEnum, null);
    }

    public static <T> Result<T> fail(ResultCodeEnum codeEnum, T data) {
        return new Result<>(codeEnum.getCode(), codeEnum.getMsg(), data);
    }
}
