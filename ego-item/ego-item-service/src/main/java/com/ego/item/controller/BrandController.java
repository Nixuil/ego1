package com.ego.item.controller;

import com.ego.item.pojo.Brand;
import com.ego.item.vo.PageVo;
import com.ego.item.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 **/
//http://api.ego.com/api/item/brand
@RestController
@RequestMapping("brand")
@Slf4j
public class BrandController {

    @Resource
    private BrandService brandService;

    @GetMapping("/page")
    public ResponseEntity<PageVo<Brand>> page(
            @RequestParam(value = "pageNo", defaultValue = "1") Long pageNo,
            @RequestParam(value = "pageSize",defaultValue = "5") Long pageSize,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "descending",defaultValue = "true") Boolean descending,
            @RequestParam(value = "key",required = false) String key) {
        PageVo<Brand> page = brandService.page(pageNo,pageSize,sortBy,descending,key);
        if (page.getTotal() == null || page.getItems() == null){
            log.info("查询数据为空");
            return ResponseEntity.noContent().build();
        }
        log.info("查询成功，数据{},总条数{}",page.getItems(),page.getTotal());
        return ResponseEntity.ok(page);

    }
    @PostMapping
    public ResponseEntity save(Brand brand,@RequestParam("cids") Long[] cids){
        if (null == brand.getLetter() || null == brand.getName() || cids.length == 0){
            log.error("数据不合法");
            return ResponseEntity.status(400).build();
        }
        try {
            brandService.save(brand,cids);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity edit(Brand brand,@RequestParam("cids") Long[] cids){
        if (null == brand.getLetter() || null == brand.getName() || cids.length == 0){
            log.error("数据不合法");
            return ResponseEntity.status(400).build();
        }
        try {
            brandService.update(brand,cids);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity delete(@RequestParam Long id){
        try {
            brandService.delete(id);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }

//    ttp://api.ego.com/api/item/brand/cid/120
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> brandList(@PathVariable Long cid){
        List<Brand> brandList = brandService.listByCid(cid);
        if (CollectionUtils.isEmpty(brandList)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(brandList);
    }

    @GetMapping("/list/ids")
    public ResponseEntity<List<Brand>> queryBrandListByIds(@RequestParam("ids") List<Long> idList){
        List<Brand> brandList =  brandService.selectBrandsByIds(idList);
        return ResponseEntity.ok(brandList);
    }
}
