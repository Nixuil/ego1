package com.ego.item.service.impl;

import com.ego.common.enums.ExceptionEnum;
import com.ego.common.enums.IException;
import com.ego.common.exception.EgoException;
import com.ego.item.mapper.SpecMapper;
import com.ego.item.pojo.Specification;
import com.ego.item.service.SpecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 *
 **/
@Service
@Slf4j
public class SpecServiceImpl implements SpecService {
    @Resource
    private SpecMapper specMapper;
    @Override
    @Transactional(readOnly = true)
    public Specification selectByCid(Integer cid) {
        try {
            Specification specification = specMapper.selectById(cid);
            log.info("查找成功",specification);
            return specification;
        }catch (Exception e){
            log.error("查找规格失败",e);
            throw new EgoException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(Specification specification) {
        try {
            specMapper.insert(specification);
            log.info("新增成功",specification);
        }catch (Exception e){
            log.error("新增规格失败",e);
            throw new EgoException(ExceptionEnum.SPEC_GROUP_CREATE_FAILED);
        }
    }

    @Override
    public void update(Specification specification) {
        try {
            specMapper.updateById(specification);
            log.info("更新成功",specification);
        }catch (Exception e){
            log.error("更新规格失败",e);
            throw new EgoException(ExceptionEnum.UPDATE_SPEC_GROUP_FAILED);
        }
    }
}
