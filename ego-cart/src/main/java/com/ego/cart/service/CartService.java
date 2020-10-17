package com.ego.cart.service;

import com.ego.auth.entity.UserInfo;
import com.ego.cart.client.GoodClient;
import com.ego.cart.filter.LoginInterceptor;
import com.ego.cart.pojo.Cart;
import com.ego.common.util.JsonUtils;
import com.ego.item.pojo.Sku;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 **/
@Service
public class CartService {
    static final String KEY_PREFIX = "ego:cart:uid:";
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private GoodClient goodsClient;
    private HashOperations<String, Object, Object> opsForHash;
    @PostConstruct
    public void init(){
       opsForHash = this.stringRedisTemplate.opsForHash();
    }
    public void addCart(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userIdStr = userInfo.getId().toString();
        String skuIdStr = cart.getSkuId().toString();
        Sku sku = goodsClient.querySkuById(cart.getSkuId()).getBody();
        //判断redis中sku是否存在
        Boolean aBoolean = opsForHash.hasKey(KEY_PREFIX + userIdStr, skuIdStr);
        if (aBoolean){
            //存在
            String cartJson = (String) opsForHash.get(KEY_PREFIX + userIdStr, skuIdStr);
            Cart cartFromRedis = JsonUtils.parse(cartJson, Cart.class);
            cartFromRedis.setNum(cartFromRedis.getNum()+cart.getNum());
            cart = cartFromRedis;
        }else {
            cart = Cart.builder().skuId(cart.getSkuId())
                    .userId(userInfo.getId())
                    .image(sku.getImages())
                    .num(cart.getNum())
                    .ownSpec(sku.getOwnSpec())
                    .price(sku.getPrice())
                    .title(sku.getTitle())
                    .build();
        }
        stringRedisTemplate.opsForHash().put(KEY_PREFIX+userInfo.getId(),skuIdStr,JsonUtils.serialize(cart));
    }

    public List<Cart> getCartList() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        List<Cart> carts = opsForHash.values(KEY_PREFIX + userInfo.getId())
                .stream()
                .map(cart -> JsonUtils.parse((String) cart, Cart.class))
                .collect(Collectors.toList())
                ;
        return carts;
    }

    public void updateNum(Long skuId, Integer num) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String cartJson = (String) opsForHash.get(KEY_PREFIX + userInfo.getId(), skuId.toString());
        Cart cartFromRedis = JsonUtils.parse(cartJson, Cart.class);
        cartFromRedis.setNum(num);
        opsForHash.put(KEY_PREFIX+userInfo.getId(),skuId.toString(),JsonUtils.serialize(cartFromRedis));
    }

    public void deleteCart(Long skuId) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        opsForHash.delete(KEY_PREFIX + userInfo.getId(), skuId.toString());
    }

    public List<Cart> saveStorageCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            addCart(cart);
        }
        return getCartList();
    }
}
