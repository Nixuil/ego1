package com.ego.item.controller;

import com.ego.item.pojo.Specification;
import com.ego.item.service.SpecService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 *
 **/
@Controller
@RequestMapping("/spec")
public class SpecController {
    @Resource
    private SpecService specService;

    @GetMapping("/{cid}")
    public ResponseEntity<String> specificationList(@PathVariable Integer cid) {
        Specification specification = specService.selectByCid(cid);
        if (specification == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(specification.getSpecifications());
    }

    @PostMapping
    public ResponseEntity<String> saveSpecification( Specification specification) {
        specService.save(specification);
        return ResponseEntity.ok().build();
    }
    @PutMapping
    public ResponseEntity<String> updateSpecification( Specification specification) {
        specService.update(specification);
        return ResponseEntity.ok().build();
    }
}
