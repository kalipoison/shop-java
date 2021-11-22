package com.gohb.feign;

import com.gohb.domain.Basket;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(value = "cart-service")
public interface OrderCartFeign {

    /**
     * 远程调用购物车 拿到购物车的商品集合
     *
     * @param basketIds
     * @return
     */
    @PostMapping("getBasketByIds")
    List<Basket> getBasketByIds(@RequestParam List<Long> basketIds);

    /**
     * 远程调用根据用户id和skuIds清空购物车
     *
     * @param openId
     * @param skuIds
     */
    @PostMapping("clearCart")
    void clearCart(@RequestParam("openId") String openId, @RequestParam("skuIds") List<Long> skuIds);


}
