package org.czg.excel.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.czg.excel.enums.ResultCodeEnum;

/**
 * @author czg
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BizException extends RuntimeException {

    private String code;

    private String message;

    public BizException(String message) {
        this.code = ResultCodeEnum.BIZ_ERROR.getCode();
        this.message = message;
    }

    public BizException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BizException(String message, String code, String message1) {
        super(message);
        this.code = code;
        this.message = message1;
    }

    public BizException(String message, Throwable cause, String code, String message1) {
        super(message, cause);
        this.code = code;
        this.message = message1;
    }

    public BizException(Throwable cause, String code, String message) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String message1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.message = message1;
    }
}
