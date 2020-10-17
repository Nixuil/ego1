package com.ego.item.vo;

import com.ego.item.pojo.Sku;
import com.ego.item.pojo.Spu;
import com.ego.item.pojo.SpuDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 *
 **/
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor //全参构造函数
@NoArgsConstructor //空参构造函数
public class SpuVo extends Spu {
    private String categoryNames;
    private String brandName;
    private List<Sku> skus;
    private SpuDetail spuDetail;
}
