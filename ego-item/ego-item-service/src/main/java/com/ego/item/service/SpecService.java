package com.ego.item.service;

import com.ego.item.pojo.Specification;

public interface SpecService {
    /**
     * 查询规格参数
     * @param cid
     * @return
     */
    Specification selectByCid(Integer cid);

    /**
     * 添加一个规范参数
     * @param specification
     */
    void save(Specification specification);

    /**
     * 更新
     * @param specification
     */
    void update(Specification specification);
}
