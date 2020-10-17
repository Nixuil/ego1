package com.ego.search.service;

import com.ego.common.enums.ExceptionEnum;
import com.ego.common.exception.EgoException;
import com.ego.common.util.JsonUtils;
import com.ego.common.util.NumberUtils;
import com.ego.item.pojo.*;
import com.ego.item.vo.SpuVo;
import com.ego.search.bo.SearchRequest;
import com.ego.search.bo.SearchResult;
import com.ego.search.client.BrandClient;
import com.ego.search.client.CategoryClient;
import com.ego.search.client.GoodsClient;
import com.ego.search.client.SpecificationClient;
import com.ego.search.pojo.Goods;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.metrics.ParsedStats;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Service
@Slf4j
public class SearchService {

    @Resource
    private SpecificationClient specificationClient;

    @Resource
    private CategoryClient categoryClient;

    @Resource
    private BrandClient brandClient;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public Goods buildGoods(SpuVo spuVo) {
        try {
            List<Sku> skus = spuVo.getSkus();
            //获取到当前spu的specifications，解析到其中所有可搜索的规格参数，存入到spec中
            SpuDetail spuDetail = spuVo.getSpuDetail();
            String specifications = spuDetail.getSpecifications();
            List<Map<String, Object>> groupList = objectMapper.readValue(specifications, new TypeReference<List<Map<String, Object>>>() {
            });

            Map<String, Object> specs = new HashMap<>();
            //循环遍历groupList
            groupList.forEach(group -> {
                List<Map<String, Object>> params = (List<Map<String, Object>>) group.get("params");
                params.forEach(param -> {
                    //判断里面每个元素是否可搜索
                    Boolean searchable = (Boolean) param.get("searchable");
                    if (searchable != null && searchable) {
                        String k = (String) param.get("k");
                        Object v = param.get("v");
                        //如果没有v，那么就存options
                        if (v != null) {
                            specs.put(k, v);
                        } else {
                            specs.put(k, param.get("options"));
                        }
                    }
                });
            });

            Goods result = Goods.builder()
                    .id(spuVo.getId())
                    .cid1(spuVo.getCid1())
                    .cid2(spuVo.getCid2())
                    .cid3(spuVo.getCid3())
                    .brandId(spuVo.getBrandId())
                    .all(spuVo.getTitle() + " " + spuVo.getCategoryNames() + " " + spuVo.getBrandName())
                    .createTime(spuVo.getCreateTime())
                    .subTitle(spuVo.getSubTitle())
                    .skus(objectMapper.writeValueAsString(skus))
                    .specs(specs)
                    .price(skus.stream().map(sku -> sku.getPrice()).collect(Collectors.toList()))
                    .build();
            return result;
        } catch (Exception e) {
            log.error("将SpuVo转换成Goods异常", e);
            //TODO
            throw new EgoException(ExceptionEnum.SPEC_PARAM_CREATE_FAILED);
        }
    }


    public SearchResult page(SearchRequest searchRequest) {
        SearchResult result = new SearchResult();

        String key = searchRequest.getKey();
        Integer pageNo = searchRequest.getPageNo();
        Integer size = searchRequest.getSize();

        //关键字如果为空，不能查询
        if (StringUtils.isBlank(key)) {
            return null;
        }
        //默认值当前页为1
        if (pageNo == null) {
            pageNo = 1;
        }
        if (size == null) {
            size = 20;
        }
        //同于Query，提取match_all
        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key);

        //自定查询
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(basicQuery);
        //指定字段查询
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "title", "subTitle", "skus"}, null));
        if (searchRequest.getSortBy() != null) {
            nativeSearchQueryBuilder.withSort(new FieldSortBuilder(searchRequest.getSortBy()));
        }
        //关键字查询
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("all", key));

        //分页查询
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNo - 1, size));

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("分类").field("cid3"));
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("品牌").field("brandId"));
        //执行查询

        SearchHits<Goods> allHits = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), Goods.class);
        nativeSearchQueryBuilder = addSearchFilter(nativeSearchQueryBuilder,searchRequest);
        SearchHits<Goods> search = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), Goods.class);
        //将搜索返回的SearchHit对象 转换成 Goods对象
        List<Goods> items = search.getSearchHits()
                .stream()
                .map(goodsSearchHit -> goodsSearchHit.getContent())
                .collect(Collectors.toList());

        result.setItems(items);
        result.setSize(size);
        result.setTotal(search.getTotalHits());
        result.setTotalPage(result.getTotalPage());

        //分类和品牌进入通过id进入mysql数据库查询
        List<Category> category = getCategory(allHits);
        List<Brand> brands = getBrands(allHits);
        result.setCategories(category);
        result.setBrands(brands);

        if (!CollectionUtils.isEmpty(category)) {
            List<Map<String, Object>> specs = getSpecs(category.get(0).getId(), basicQuery);
            result.setSpecs(specs);
        }

        return result;
    }

    private NativeSearchQueryBuilder addSearchFilter(NativeSearchQueryBuilder nativeSearchQueryBuilder,SearchRequest searchRequest) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        searchRequest.getFilters().forEach((k,v)->{
            if("cid3".equals(k)){
                queryBuilder.must(QueryBuilders.termQuery(k, v));
            }
            else if("brandId".equals(k)){
                queryBuilder.must(QueryBuilders.termQuery(k, v));
            }
            else{
                boolean matches = Pattern.matches("^[0-9]*([.][0-9])?-[1-9][0-9]*([.][0-9])?$", v);
                if (matches){
                    String[] split = v.split("-");
                    queryBuilder.must(QueryBuilders.rangeQuery("specs."+k).gte(split[0]).lt(split[1]));
                }else {
                    queryBuilder.must(QueryBuilders.matchQuery("specs."+k+".keyword",v));
                }
            }
        });
        return nativeSearchQueryBuilder.withQuery(QueryBuilders.boolQuery().filter(queryBuilder));

    }

    /**
     * 得到其他规格参数过滤条件
     *
     * @param id
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getSpecs(Long id, QueryBuilder basicQuery) {
        //分类不同类型规格参数,进行不同的处理
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String> numSpecs = new HashMap<>();
        List<String> strSpecs = new ArrayList<>();
        ResponseEntity<String> entity = specificationClient.querySpecJsonByCid(id);
        String specJson = entity.getBody();
        List<Map<String, Object>> specs = JsonUtils.nativeRead(specJson, new TypeReference<List<Map<String, Object>>>() {
        });
        specs.forEach(spec -> {
                    if (spec.get("params") != null || spec.get("params") == "") {
                        ((List<Map<String, Object>>) spec.get("params")).forEach(param -> {
                            //判断是否可查询
                            if ((boolean) param.get("searchable")) {
                                //得到key
                                String k = (String) param.get("k");
                                //得到当前param是否为数字的布尔值
                                Object numerical = param.get("numerical");
                                if (numerical != null && (boolean) numerical) {
                                    String unit = (String) param.get("unit");
                                    numSpecs.put(k, unit);
                                } else
                                    strSpecs.add(k);
                            }
                        });
                    }
                }
        );

        //得到数字型参数的间隔
        Map<String, Double> intervalMap = getIntervalMap(numSpecs, basicQuery);
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);

        //从elasticsearch查询所有规范查询数据
        strSpecs.forEach(spec -> { queryBuilder.addAggregation(AggregationBuilders.terms(spec).field("specs." + spec + ".keyword")); });
        numSpecs.forEach((k, unit) -> {
            if (intervalMap.get(k) > 0) {
                queryBuilder.addAggregation(AggregationBuilders.histogram(k).interval(intervalMap.get(k)).field("specs." + k));
            }
        });
        SearchHits<Goods> hits = elasticsearchRestTemplate.search(queryBuilder.build(), Goods.class);
        result = parseAgg(hits.getAggregations(), strSpecs, numSpecs, intervalMap);
        return result;
    }

    //最终处理数据返回
    private List<Map<String, Object>> parseAgg(Aggregations aggregations, List<String> strSpecs, Map<String, String> numSpecs, Map<String, Double> intervalMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        //解析数字数据
        numSpecs.forEach((k, unit) -> {
            ParsedHistogram aggregation = aggregations.get(k);
            if (aggregation != null) {
                Map<String, Object> map = new HashMap<>();
                List<String> options = aggregation.getBuckets().stream().map(bucket -> {
                    String option = null;
                    //区间拼接成字符串
                    Double start = (Double) bucket.getKey();
                    Double end = start + intervalMap.get(k);
                    //判断start是否为整数
                    if (NumberUtils.isInt(start) && NumberUtils.isInt(end)) {
                        option = start.intValue() + "-" + end.intValue();
                    } else {
                        option = NumberUtils.scale(start, 1) + "-" + NumberUtils.scale(end, 1);
                    }
                    return option;
                }).collect(Collectors.toList());
                map.put("options", options);
                map.put("name", k);
                map.put("unit", unit);
                result.add(map);
            }
        });
        //解析字符串数据
        strSpecs.forEach(strSpec -> {
            ParsedStringTerms aggregation = aggregations.get(strSpec);
            if (aggregation != null) {
                Map<String, Object> map = new HashMap<>();
                List<String> collect = aggregation.getBuckets().stream().map(bucket ->
                        bucket.getKeyAsString()
                ).filter(option -> StringUtils.isNotBlank(option)).collect(Collectors.toList());
                map.put("options", collect);
                map.put("name", strSpec);
                result.add(map);
            }
        });
        return result;
    }

    /**
     * 获取间隔
     *
     * @param numSpecs
     * @param basicQuery
     * @return
     */
    private Map<String, Double> getIntervalMap(Map<String, String> numSpecs, QueryBuilder basicQuery) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        //结果集
        Map<String, Double> result = new HashMap<>(numSpecs.size());
        //所有字段的聚合
        numSpecs.forEach((k, unit) -> {
            queryBuilder.addAggregation(AggregationBuilders.stats(k).field("specs." + k));
        });
        //不要hits
        queryBuilder.withPageable(PageRequest.of(0, 1));
        SearchHits<Goods> search = elasticsearchRestTemplate.search(queryBuilder.build(), Goods.class);
        //将所有数字字段的聚合数据一一取出.
        numSpecs.forEach((k, unit) -> {
            ParsedStats aggregation = search.getAggregations().get(k);
            double interval = NumberUtils.getInterval(aggregation.getMin(), aggregation.getMax(), aggregation.getSum());
            result.put(k, interval);
        });
        return result;
    }

    private List<Brand> getBrands(SearchHits<Goods> search) {
        ParsedLongTerms aggregation = search.getAggregations().get("品牌");
        List<Long> buckets = aggregation.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        ResponseEntity<List<Brand>> entity = brandClient.queryBrandListByIds(buckets);
        return entity.getBody();
    }

    private List<Category> getCategory(SearchHits<Goods> search) {
        ParsedLongTerms aggregation = search.getAggregations().get("分类");
        List<Long> buckets = aggregation.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        ResponseEntity<List<Category>> entity = categoryClient.queryCategoryByIds(buckets);
        return entity.getBody();
    }

}
