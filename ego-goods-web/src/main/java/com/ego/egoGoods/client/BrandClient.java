package com.ego.egoGoods.client;

import com.ego.item.api.BrandApi;
import com.ego.item.pojo.Brand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 *
 **/
@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
