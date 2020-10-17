package com.ego.item.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2020/9/18
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@TableName("tb_brand")
@Data
public class Brand {
    @TableId(type=IdType.AUTO)
    private Long id;
    private String name;// 品牌名称
    private String image;// 品牌图片
    private Character letter;
}
