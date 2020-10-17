package com.ego.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ego.item.mapper.BrandMapper;
import com.ego.item.pojo.Brand;
import com.ego.item.vo.PageVo;
import com.ego.item.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 **/
@Service
@Slf4j
public class BrandServiceImpl implements BrandService {
    @Resource
    private BrandMapper brandMapper;
    @Override
    public PageVo<Brand> page(Long pageNo, Long pageSize, String sortBy, Boolean descending, String key) {
        QueryWrapper<Brand> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(sortBy)){
            wrapper.orderBy(true,descending,sortBy);
        }
        if (StringUtils.isNotBlank(key)){
            wrapper.like("name",key).or().eq("letter",key);
        }
        Page<Brand> brandPage = brandMapper.selectPage(new Page<Brand>(pageNo, pageSize), wrapper);
        return new PageVo<Brand>(brandPage.getRecords(),brandPage.getTotal());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(Brand brand, Long[] cids) {
        brandMapper.insert(brand);
        for (Long cid : cids) {
            brandMapper.insertBrandCategory(brand.getId(),cid);
        }
    }

    @Override
    public void update(Brand brand, Long[] cids) {
        brandMapper.updateById(brand);
        brandMapper.deleteBrandCategory(brand.getId());
        for (Long cid : cids) {
            brandMapper.insertBrandCategory(brand.getId(),cid);
        }
    }

    @Override
    public void delete(Long id) {
        brandMapper.deleteBrandCategory(id);
        brandMapper.deleteById(id);
        log.info("删除完成商品id为{}",id);
    }

    @Override
    public List<Brand> listByCid(Long cid) {
        List<Brand> brandList = brandMapper.selectListByCid(cid);
        return brandList;
    }

    @Override
    public List<Brand> selectBrandsByIds(List<Long> idList) {
        List<Brand> brandList = brandMapper.selectBatchIds(idList);
        return brandList;
    }
}
