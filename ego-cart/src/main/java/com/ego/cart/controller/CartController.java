package com.ego.cart.controller;

import com.ego.cart.config.JwtProperties;
import com.ego.cart.pojo.Cart;
import com.ego.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 **/
@Slf4j
@RestController
public class CartController {
    @Resource
    private CartService cartService;
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        try {
            this.cartService.addCart(cart);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping
    public ResponseEntity<List<Cart>> getCartList(){
        try {
            List<Cart> carts = this.cartService.getCartList();
            return ResponseEntity.ok(carts);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestParam("skuId") Long skuId,
                                          @RequestParam("num") Integer num) {
        try {
            this.cartService.updateNum(skuId, num);
        }catch (Exception e){
            log.error("更新购物车数量异常");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteCart(@RequestParam("skuId") Long skuId){
        try {
            this.cartService.deleteCart(skuId);
        }catch (Exception e){
            log.error("删除购物项异常");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saveStorage")
    public ResponseEntity<List<Cart>> saveStorageCart(@RequestBody List<Cart> cartList) {
        try {
            List<Cart> carts = this.cartService.saveStorageCart(cartList);
            return ResponseEntity.ok(carts);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
