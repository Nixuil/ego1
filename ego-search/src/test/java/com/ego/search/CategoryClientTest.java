package com.ego.search;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Category;
import com.ego.item.vo.SpuVo;
import com.ego.search.client.GoodsClient;
import com.ego.search.pojo.Goods;
import com.ego.search.repository.GoodsRepository;
import com.ego.search.service.SearchService;
import com.netflix.discovery.shared.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Resource
    private GoodsClient goodsClient;
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private SearchService searchService;
    @Test
    public void importDataFromMysql(){
        int size = 0;
        int page = 1;
        do{
        PageResult<SpuVo> pageResult = goodsClient.page("", true, page++, 100).getBody();
        List<SpuVo> spuBoList = pageResult.getItems();
        size = spuBoList.size();
//        将List<SpuVo>  ->  List<Goods>
            List<Goods> goodsList = spuBoList.stream()
                    .map(spuBo -> searchService.buildGoods(spuBo))
                    .collect(Collectors.toList());
            //将100条数据存入es
            goodsRepository.saveAll(goodsList);
        }while (size==100);
    }
}
