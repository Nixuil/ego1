package com.ego.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ego.item.pojo.Category;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {

    @Select("select c.* from tb_brand as b,tb_category as c,tb_category_brand as cb where b.id=#{bid} and cb.category_id = c.id and cb.brand_id = b.id;")
    public List<Category> selectByBid(Integer bid);
}
