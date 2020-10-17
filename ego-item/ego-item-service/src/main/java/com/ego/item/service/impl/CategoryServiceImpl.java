package com.ego.item.service.impl;

import com.ego.item.mapper.CategoryMapper;
import com.ego.item.pojo.Category;
import com.ego.item.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 *  商品类别实现层
 **/
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Category> list(Integer pid) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("parent_id",pid);
        return categoryMapper.selectByMap(map);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(Category category) throws Exception {
        try {
            categoryMapper.insert(category);
            log.info("新增成功{}",category);
        }catch (Exception e){
            log.error("新增失败{}",e);
            throw new Exception();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void edit(Integer id,String name) throws Exception {
        try {
            Category category = categoryMapper.selectById(id);
            category.setName(name);
            categoryMapper.updateById(category);
            log.info("修改成功{}",category);
        }catch (Exception e){
            log.error("修改失败{}",e);
            throw new Exception();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Integer id) throws Exception {
        try {
            categoryMapper.deleteById(id);
            log.info("删除成功{}",id);
        }catch (Exception e){
            log.error("删除失败{}",e);
            throw new Exception();
        }
    }

    @Override
    public List<Category> selectCategoryByBid(Integer id) {
        List<Category> categoryList = categoryMapper.selectByBid(id);
        return categoryList;
    }

    @Override
    public List<Category> selectCategoryByIds(List<Long> idList) {
        List<Category> categoryList = categoryMapper.selectBatchIds(idList);
        return categoryList;
    }
}
