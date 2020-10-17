package com.ego.item.controller;

import com.ego.item.pojo.Category;
import com.ego.item.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 **/
//http://api.ego.com/api/item/category/list?pid=0
@RestController
@RequestMapping("/category")
@Slf4j
public class CategotyController {

    @Resource
    private CategoryService categoryService;

    /**
     * 根据父id查询
     * @param pid
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> list(@RequestParam("pid") Integer pid) {
        List<Category> categoryList = categoryService.list(pid);
        log.info("查询数据为{}",categoryList);
        if (categoryList == null || categoryList.size() == 0){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categoryList);
    }

    /**
     * 新增
     * @param category
     * @return
     */
    @PostMapping
    public ResponseEntity save(@RequestBody Category category){
        if (null==category){
            log.error("新增数据为空");
            return ResponseEntity.status(500).build();
        }
        try {
            categoryService.save(category);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }
    /**
     * 编辑
     */
    @PutMapping
    public ResponseEntity edit(@RequestParam("id") Integer id, @RequestParam("name") String name){
        try {
            categoryService.edit(id,name);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }
    /**
     * 删除
     */
    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable Integer id){
        try {
            categoryService.delete(id);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bid/{id}")
    public List<Category> getCategoryByBid(@PathVariable Integer id){
        List<Category> categoryList = categoryService.selectCategoryByBid(id);
        return categoryList;
    }

    @GetMapping("/list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> idList){
       List<Category> categorylist =  categoryService.selectCategoryByIds(idList);
       return ResponseEntity.ok(categorylist);
    }


}
