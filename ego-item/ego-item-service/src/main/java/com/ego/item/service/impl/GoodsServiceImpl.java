package com.ego.item.service.impl;

import com.ego.common.enums.ExceptionEnum;
import com.ego.common.exception.EgoException;
import com.ego.common.pojo.PageResult;
import com.ego.item.mapper.SkuMapper;
import com.ego.item.mapper.SpuDetailMapper;
import com.ego.item.mapper.SpuMapper;
import com.ego.item.mapper.StockMapper;
import com.ego.item.pojo.Sku;
import com.ego.item.service.GoodsService;
import com.ego.item.vo.SpuVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *
 **/
@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private SpuMapper spuMapper;
    @Resource
    private SpuDetailMapper spuDetailMapper;
    @Resource
    private SkuMapper skuMapper;
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PageResult<SpuVo> getPage(String key, Boolean saleable, Integer page, Integer rows) {
        List<SpuVo> spuVos = spuMapper.selectPage1("%"+key+"%",saleable,(page-1)*rows,rows);
        Integer integer = spuMapper.selectCount(key, saleable);
        PageResult<SpuVo> pageResult = new PageResult<SpuVo>(integer.longValue(), spuVos);
        return pageResult;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(SpuVo spuVo) {
        try {
            spuMapper.insert(spuVo);
        }catch (Exception e){
            log.error("添加spu参数失败",e);
            throw new EgoException(ExceptionEnum.SPEC_PARAM_CREATE_FAILED);
        }
        try {
            spuVo.getSpuDetail().setSpuId(spuVo.getId());
            spuDetailMapper.insert(spuVo.getSpuDetail());
        }catch (Exception e){
            log.error("添加spu_detail参数失败",e);
            throw new EgoException(ExceptionEnum.SPEC_PARAM_CREATE_FAILED);
        }
        try {
            for (Sku sku : spuVo.getSkus()) {
                sku.setCreateTime(new Date());
                sku.setLastUpdateTime(new Date());
                sku.setSpuId(spuVo.getId());
                skuMapper.insert(sku);
            }
            log.info("新增sku成功");
        }catch (Exception e){
            log.error("添加sku属性失败",e);
            throw new EgoException(ExceptionEnum.SKU_SAVE_ERROR);
        }
    }

    @Override
    public SpuVo queryGoodsById(Long id) {
        return this.spuMapper.selectSpuBoById(id);
    }

    @Override
    public Sku querySkuById(Long id) {
        return skuMapper.selectById(id);
    }


}
