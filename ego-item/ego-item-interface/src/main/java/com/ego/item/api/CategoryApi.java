package com.ego.item.api;

import com.ego.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 *
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2020/9/28
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@RequestMapping("/category")
public interface CategoryApi {

    @GetMapping("/list/ids")
    ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> idList);
}
