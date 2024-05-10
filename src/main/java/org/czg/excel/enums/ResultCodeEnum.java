package org.czg.excel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author czg
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ResultCodeEnum {
    SUCCESS("0000", "请求成功"),
    SERVER_ERROR("5000", "系统异常"),
    BIZ_ERROR("1000", "业务处理异常")
    ;
    private String code;

    private String msg;

}
