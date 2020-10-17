package com.ego.item.service;

import com.ego.item.pojo.Brand;
import com.ego.item.vo.PageVo;

import java.util.List;

public interface BrandService {
    /**
     * 分页查询
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @param descending
     * @param key
     * @return
     */
    PageVo<Brand> page(Long pageNo, Long pageSize, String sortBy, Boolean descending, String key);

    /**
     * 新增
     * @param brand
     * @param cids
     */
    void save(Brand brand, Long[] cids);

    /**
     * 更新
     * @param brand
     * @param cids
     */
    void update(Brand brand, Long[] cids);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 根据cid查询
     * @param cid
     * @return
     */
    List<Brand> listByCid(Long cid);

    /**
     * 根据主键ids查询
     * @param idList
     * @return
     */
    List<Brand> selectBrandsByIds(List<Long> idList);
}
