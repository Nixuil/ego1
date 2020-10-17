package com.ego.item.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 */
@Data
@TableName("tb_specification")
public class Specification {
    @TableId(type = IdType.INPUT)
    private Long categoryId;
    private String specifications;
}
