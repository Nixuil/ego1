package com.ego.item.service;

import com.ego.item.pojo.Category;

import java.util.List;

/**
 *
 **/
public interface CategoryService {
    /**
     * 根据父id查询商品类别
     * @param pid
     * @return
     */
    public List<Category> list(Integer pid);

    /**
     * 新增
     * @param category
     */
    void save(Category category) throws Exception;
    /**
     * 编辑
     */
    void edit(Integer id,String name) throws Exception;
    /**
     * 删除
     */
    void delete(Integer id) throws Exception;

    /**
     * 通过bid查询cid
     * @param id
     * @return
     */
    List<Category> selectCategoryByBid(Integer id);

    /**
     * 通过类别id查询
     * @param idList
     * @return
     */
    List<Category> selectCategoryByIds(List<Long> idList);
}
