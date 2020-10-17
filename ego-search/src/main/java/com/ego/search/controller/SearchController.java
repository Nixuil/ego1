package com.ego.search.controller;


import com.ego.search.bo.SearchRequest;
import com.ego.search.bo.SearchResult;
import com.ego.search.service.SearchService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 *
 */
@RestController
public class SearchController {
    @Resource
    private SearchService searchService;

    @PostMapping("/page")
    public ResponseEntity<SearchResult> page(@RequestBody SearchRequest searchRequest){
        SearchResult result = searchService.page(searchRequest);
        if(result==null){
            return ResponseEntity.badRequest().build();
        }
//        if(CollectionUtils.isEmpty(result.getItems())){
//            return ResponseEntity.noContent().build();
//        }
        return ResponseEntity.ok(result);
    }

}
