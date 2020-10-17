package com.ego.item.controller;

import com.ego.common.pojo.CartDto;
import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Sku;
import com.ego.item.pojo.Spu;
import com.ego.item.service.GoodsService;
import com.ego.item.vo.SpuVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 **/
//goods/spu/page?key=&saleable=false&page=1&rows=5
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuVo>> getPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    ){
        PageResult<SpuVo> spuPageResult = goodsService.getPage(key, saleable, page, rows);
        if (spuPageResult.getItems().size()==0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(spuPageResult);
    }

    @PostMapping
    public ResponseEntity save(@RequestBody SpuVo spuVo){
        Spu spu = spuVo;
        goodsService.save(spuVo);
        return ResponseEntity.ok().build();
    }
    /**
     * 根据id查询商品
     * @param id
     * @return
     */
    @GetMapping("/spubo/{id}")
    public ResponseEntity<SpuVo> queryGoodsById(@PathVariable("id") Long id){
        SpuVo spuBo=this.goodsService.queryGoodsById(id);
        if (spuBo == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(spuBo);
    }

    @GetMapping("/sku/{id}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("id") Long id){
        Sku sku = this.goodsService.querySkuById(id);
        if (sku == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(sku);
    }

    /**
     * 减库存
     * @param cartDTOS
     */
    //TODO
    @PostMapping("stock/decrease")
    void decreaseStock(@RequestBody List<CartDto> cartDTOS){}
}
