package org.czg.excel.annotation;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * 数字类型校验器
 *
 * @author czg
 */
@Slf4j
public class IntValueValidator implements ConstraintValidator<IntValue, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        // 获取注解属性值
        Map<String, Object> attributes = ((ConstraintValidatorContextImpl) constraintValidatorContext)
                .getConstraintDescriptor().getAttributes();
        // 可以允许为空
        if (null != obj) {
            int input;
            try {
                // 如果传入的非数字，校验失败
                input = Integer.parseInt(obj.toString());
            } catch (Exception ignore) {
                return false;
            }
            int max = Integer.parseInt(attributes.get("max").toString());
            // 如果传入的值大于配置的值，校验失败
            return input <= max;
        }
        return true;
    }
}