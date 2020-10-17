package com.ego.item.api;

import com.ego.common.pojo.CartDto;
import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Sku;
import com.ego.item.vo.SpuVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 **/
@RequestMapping("/goods")
public interface GoodsApi {
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuVo>> page(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    );

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SpuVo spuBo);

    @GetMapping("/spubo/{id}")
    SpuVo queryGoodsById(@PathVariable("id") Long id);

    @GetMapping("/sku/{id}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("id") Long id);
    /**
     * 减库存
     * @param cartDTOS
     */
    @PostMapping("stock/decrease")
    void decreaseStock(@RequestBody List<CartDto> cartDTOS);

}
