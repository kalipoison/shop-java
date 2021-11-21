package com.gohb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.Basket;
import com.gohb.vo.CartMoney;
import com.gohb.vo.ShopCartResult;

import java.util.List;


public interface BasketService extends IService<Basket> {


    /**
     * 查询购物车商品数量
     *
     * @param openId
     * @return
     */
    Integer findCartCount(String openId);

    /**
     * 修改购物车商品数量
     *
     * @param basket
     */
    void changeItem(Basket basket);

    /**
     * 查询购物车详情
     *
     * @param openId
     * @return
     */
    List<ShopCartResult> getCartInfo(String openId);

    /**
     * 计算购物车选中商品的总金额
     *
     * @param basketIds
     * @return
     */
    CartMoney calcMoney(List<Long> basketIds);

    /**
     * 远程调用根据用户id和skuIds清空购物车
     *
     * @param openId
     * @param skuIds
     */
    void clearCart(String openId, List<Long> skuIds);
}
