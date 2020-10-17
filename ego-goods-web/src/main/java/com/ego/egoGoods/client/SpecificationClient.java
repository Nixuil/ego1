package com.ego.egoGoods.client;

import com.ego.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 *
 **/
@FeignClient(value = "item-service")
public interface SpecificationClient extends SpecificationApi {
}
