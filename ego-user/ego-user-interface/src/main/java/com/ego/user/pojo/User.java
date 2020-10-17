package com.ego.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 *
 **/
@Data
@TableName("tb_user")
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 4,max = 30)
    private String username;

    /**
     * 密码，加密存储
     */
    @NotEmpty(message = "密码不能为空")
    @JsonIgnore
    private String password;

    /**
     * 注册手机号
     */
    @NotEmpty(message = "密码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$")
    private String phone;

    /**
     * 创建时间
     */
    private Date created;
}
