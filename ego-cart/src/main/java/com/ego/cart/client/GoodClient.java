package com.ego.cart.client;

import com.ego.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 *
 **/
@FeignClient("item-service")
public interface GoodClient extends GoodsApi {

}
