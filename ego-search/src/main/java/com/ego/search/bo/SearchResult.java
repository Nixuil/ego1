package com.ego.search.bo;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Brand;
import com.ego.item.pojo.Category;
import com.ego.search.pojo.Goods;
import lombok.Data;

import java.util.List;
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
public class SearchResult extends PageResult<Goods> {
    private List<Category> categories;
    private List<Brand> brands;
    private List<Map<String,Object>> specs;
}
