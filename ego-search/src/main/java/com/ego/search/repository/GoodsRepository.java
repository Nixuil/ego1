package com.ego.search.repository;

import com.ego.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
/**
 *
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
