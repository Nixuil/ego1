package com.ego.search.bo;

import lombok.Data;

import java.util.Map;

/**
 *
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2020/9/25
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Data
public class SearchRequest {
    private String key;
    private Integer size;
    private Map<String,String> filters;
    private String sortBy;
    private Boolean order;
    private Integer pageNo;
}
