package com.ego.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ego.item.pojo.Brand;
import com.ego.item.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    @Insert("insert into tb_category_brand (category_id,brand_id) values (#{cid},#{bid})")
    void insertBrandCategory(@Param("bid") Long bid,@Param("cid") Long cid);

    @Delete("delete from tb_category_brand where brand_id = #{id}")
    void deleteBrandCategory(Long id);
    @Select("select b.* from tb_category_brand cb,tb_brand b where cb.brand_id = b.id and cb.category_id = #{cid}")
    List<Brand> selectListByCid(Long cid);
}
