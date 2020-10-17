package com.ego.item.vo;

import lombok.Data;

import java.util.List;

/**
 *
 **/
@Data
public class PageVo<T> {
    private List<T> items;
    private Long total;



    public PageVo(List<T> items, Long total) {
        this.items = items;
        this.total = total;
    }
}
