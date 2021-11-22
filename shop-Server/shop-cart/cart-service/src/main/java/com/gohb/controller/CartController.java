package com.gohb.controller;

import com.gohb.domain.Basket;
import com.gohb.service.BasketService;
import com.gohb.vo.CartMoney;
import com.gohb.vo.ShopCartResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api("购物车管理接口")
public class CartController {


    @Autowired
    private BasketService basketService;


    @GetMapping("p/shopCart/prodCount")
    @ApiOperation("查询购物车商品数量")
    public ResponseEntity<Integer> getCartCount() {
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Integer count = basketService.findCartCount(openId);
        return ResponseEntity.ok(count);
    }


    @PostMapping("p/shopCart/changeItem")
    @ApiOperation("修改购物车商品数量")
    public ResponseEntity<Void> changeItem(@RequestBody Basket basket) {
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        basket.setUserId(openId);
        basketService.changeItem(basket);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/p/shopCart/info")
    @ApiOperation("查询购物车详情")
    public ResponseEntity<List<ShopCartResult>> changeItem() {
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<ShopCartResult> shopCartResults = basketService.getCartInfo(openId);
        return ResponseEntity.ok(shopCartResults);
    }


    @DeleteMapping("/p/shopCart/deleteItem")
    @ApiOperation("删除购物车选中商品")
    public ResponseEntity<Void> cleanCart(@RequestBody List<Long> basketIds) {
        basketService.removeByIds(basketIds);
        return ResponseEntity.ok().build();
    }


    /**
     * 总金额 是多少
     * 满减是多少
     * 优惠是多少
     * 最终是多少
     *
     * @param basketIds
     * @return
     */
    @PostMapping("p/shopCart/totalPay")
    @ApiOperation("计算购物车选中商品的总金额")
    public ResponseEntity<CartMoney> totalPay(@RequestBody List<Long> basketIds) {
        CartMoney cartMoney = basketService.calcMoney(basketIds);
        return ResponseEntity.ok(cartMoney);
    }

    // ---------------- 远程调用代码

    /**
     * 提供远程调用购物车 拿到购物车的商品集合
     *
     * @param basketIds
     * @return
     */
    @PostMapping("getBasketByIds")
    @ApiOperation("提供远程调用根据ids查询购物车集合")
    List<Basket> getBasketByIds(@RequestParam List<Long> basketIds) {
        return basketService.listByIds(basketIds);
    }


    /**
     * 远程调用根据用户id和skuIds清空购物车
     *
     * @param openId
     * @param skuIds
     */
    @PostMapping("clearCart")
    @ApiOperation("提供远程调用根据用户id和skuIds清空购物车")
    void clearCart(@RequestParam("openId") String openId, @RequestParam("skuIds") List<Long> skuIds) {
        basketService.clearCart(openId, skuIds);
    }


}
