package org.czg.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.czg.excel.annotation.IntValue;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author czg
 */
@Data
public class UserExcelModel {

    @NotBlank(message = "用户id不能为空")
    @ExcelProperty(value = "用户id", index = 0)
    private String userId;

    @Length(max = 30, message = "用户联系电话最多30个字符")
    @NotBlank(message = "用户名称不能为空")
    @ExcelProperty(value = "用户名称", index = 1)
    private String userName;

    @IntValue(message = "用户年龄只能为数字")
    @NotBlank(message = "用户年龄不能为空")
    @ExcelProperty(value = "用户年龄", index = 2)
    private String age;

    @Length(max = 13, message = "用户联系电话最多13个字符")
    @NotBlank(message = "用户联系电话不能为空")
    @ExcelProperty(value = "用户联系电话", index = 3)
    private String phone;

    @Email(regexp = "[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+", message = "邮箱格式不正确")
    @ExcelProperty(value = "用户Email", index = 4)
    private String email;

}
