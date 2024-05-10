package org.czg.excel.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * @author czg
 */
@Builder
@Data
public class ExcelLineResult<T> {

    private Integer rowIndex;

    private T data;

    private Set<ConstraintViolation<T>> violations;

    private String bizErrorMsg;

}
