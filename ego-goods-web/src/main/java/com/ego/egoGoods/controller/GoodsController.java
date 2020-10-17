package com.ego.egoGoods.controller;

import com.ego.egoGoods.service.GoodsHtmlService;
import com.ego.egoGoods.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

/**
 *
 **/
@Controller
@RequestMapping("item")
public class GoodsController {
    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsHtmlService goodsHtmlService;
    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long id) {
        Map<String, Object> modelMap = this.goodsService.loadModel(id);
        model.addAllAttributes(modelMap);
        goodsHtmlService.asyncExecute(id);
        return "item";
    }
}
