package com.ego.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ego.item.pojo.Spu;
import com.ego.item.vo.SpuVo;

import java.util.List;

/**
 *
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2020/9/18
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
public interface SpuMapper extends BaseMapper<Spu> {

    Integer selectCount(String key, Boolean saleable);

    List<SpuVo> selectPage1(String key, Boolean saleable, Integer pageBegin, Integer rows);

    SpuVo selectSpuBoById(Long id);
}
