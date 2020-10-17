package com.ego.item.api;

import com.ego.item.pojo.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2020/9/28
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@RequestMapping("/spec")
public interface SpecificationApi {

    @GetMapping("/{cid}")
    public ResponseEntity<String> querySpecJsonByCid(@PathVariable("cid") Long cid);
    @PostMapping
    public ResponseEntity<Void> save(Specification specification) ;
}
