package org.czg.excel.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 数字类型校验注解类
 *
 * @author czg
 */
@Documented
@Inherited
@Constraint(validatedBy = IntValueValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IntValue {

    /**
     * 数字类型最大值，默认int范围
     *
     * @return 可传入的数字最大值
     */
    int max() default Integer.MAX_VALUE;

    /**
     * 校验失败描述信息
     *
     * @return 校验失败描述信息
     */
    String message();

    /**
     * 参数校验框架
     * 必须包含这个参数，不加这参数 error msg: contains Constraint annotation, but does not contain a groups parameter.
     *
     * @return 分组信息
     */
    Class<?>[] groups() default {};

    /**
     * 参数校验框架
     * 必须包含这个参数，不加这参数 error msg: contains Constraint annotation, but does not contain a groups parameter.
     */
    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        IntValue[] value();
    }

}
