package com.ego.item.service;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Sku;
import com.ego.item.vo.SpuVo;


public interface GoodsService {

    PageResult<SpuVo> getPage(String key, Boolean saleable, Integer page, Integer rows);

    void save(SpuVo spuVo);

    SpuVo queryGoodsById(Long id);

    Sku querySkuById(Long id);
}
