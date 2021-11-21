package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.Basket;
import com.gohb.domain.Sku;
import com.gohb.feign.BasketSkuFeign;
import com.gohb.mapper.BasketMapper;
import com.gohb.service.BasketService;
import com.gohb.vo.CartMoney;
import com.gohb.vo.ShopCartItem;
import com.gohb.vo.ShopCartResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BasketServiceImpl extends ServiceImpl<BasketMapper, Basket> implements BasketService {

    @Autowired
    private BasketMapper basketMapper;

    @Autowired
    private BasketSkuFeign basketSkuFeign;

    /**
     * 查询购物车商品数量
     *
     * @param openId
     * @return
     */
    @Override
    public Integer findCartCount(String openId) {
//        // 查询整个集合
//        List<Basket> baskets = basketMapper.selectList(new LambdaQueryWrapper<Basket>()
//                .eq(Basket::getUserId, openId)
//        );
//        // 计算数量
//        Integer count = baskets.stream()
//                .map(Basket::getBasketCount)
//                .reduce(Integer::sum)
//                .get();
        List<Object> objects = basketMapper.selectObjs(new QueryWrapper<Basket>()
                        .select("IFNULL(sum(basket_count),0)")
                        .eq("user_id", openId)
//                .last(" asljdkhajksdhgkashd ")
        );
        if (CollectionUtils.isEmpty(objects)) {
            return 0;
        }
        String s = objects.get(0).toString();
        int i = Integer.parseInt(s);
        return i;
    }

    /**
     * 修改购物车商品数量
     * 1. 购物车没有这个商品 就添加记录
     * 2. 如果有这个商品 修改数量 + -
     * 3. skuId openId
     * 4. 加入购物车 商品的库存不变的 在下订单的时候商品的库存才真正的减少
     *
     * @param basket
     */
    @Override
    public void changeItem(Basket basket) {
        log.info("修改购物车{}", JSON.toJSONString(basket));
        // 查询表里面有没有这一行数据
        Basket oldBasket = basketMapper.selectOne(new LambdaQueryWrapper<Basket>()
                .eq(Basket::getUserId, basket.getUserId())
                .eq(Basket::getSkuId, basket.getSkuId())
        );
        if (ObjectUtils.isEmpty(oldBasket)) {
            // 新增
            basket.setBasketDate(new Date());
            basketMapper.insert(basket);
            return;
        }
        // 不等于空 修改数量
        int finalCount = oldBasket.getBasketCount() + basket.getBasketCount();
        if (finalCount < 0) {
            // 扔一个异常
            throw new IllegalArgumentException("修改购物车数量不能小于0");
        }
        oldBasket.setBasketCount(finalCount);
        oldBasket.setBasketDate(new Date());
        // 修改操作
        basketMapper.updateById(oldBasket);
    }

    /**
     * 查询购物车详情
     * 1. 查询basket表
     * 拿到skuId --> prodId
     * 2. 根据skuIds 远程调用拿到 List<Sku>
     * 3. 组装shopCartItem数据返回
     *
     * @param openId
     * @return
     */
    @Override
    public List<ShopCartResult> getCartInfo(String openId) {
        // 1.查询basket
        List<Basket> basketList = basketMapper.selectList(new LambdaQueryWrapper<Basket>()
                .eq(Basket::getUserId, openId)
        );
        if (CollectionUtils.isEmpty(basketList)) {
            return Collections.emptyList();
        }
        // 2.有购物车 拿到skuIds
        List<Long> skuIds = basketList.stream()
                .map(Basket::getSkuId)
                .collect(Collectors.toList());

        // 3.远程调用商品模块
        List<Sku> skuList = basketSkuFeign.getSkuByIds(skuIds);

        if (CollectionUtils.isEmpty(skuList)) {
            // 扔一个异常
            throw new RuntimeException("服务器维护中,不好意思啦");
        }
        // 组装数据了
        List<ShopCartResult> shopCartResults = new ArrayList<>();
        ShopCartResult shopCartResult = new ShopCartResult();
        List<ShopCartItem> shopCartItems = new ArrayList<>();
        // 循环构建对象了
        basketList.forEach(basket -> {
            ShopCartItem shopCartItem = new ShopCartItem();

            shopCartItem.setBasketCount(basket.getBasketCount());
            shopCartItem.setSkuId(basket.getSkuId());
            shopCartItem.setBasketId(basket.getBasketId());
            shopCartItem.setChecked(Boolean.TRUE);
            shopCartItem.setProdId(basket.getProdId());

            Sku sku1 = skuList.stream()
                    .filter(sku -> sku.getSkuId().equals(basket.getSkuId()))
                    .collect(Collectors.toList())
                    .get(0);
            shopCartItem.setSkuName(sku1.getSkuName());
            shopCartItem.setPrice(sku1.getPrice().toString());
            shopCartItem.setProdName(sku1.getProdName());
            shopCartItem.setPic(sku1.getPic());
            // 添加集合
            shopCartItems.add(shopCartItem);
        });

        shopCartResult.setShopCartItems(shopCartItems);
        shopCartResults.add(shopCartResult);
        return shopCartResults;
    }

    /**
     * 计算购物车选中商品的总金额
     *
     * @param basketIds
     * @return
     */
    @Override
    public CartMoney calcMoney(List<Long> basketIds) {
        CartMoney cartMoney = new CartMoney();
        // 购物车 只有数量 没有单价 查到单价  在计算
        if (CollectionUtils.isEmpty(basketIds)) {
            return cartMoney;
        }
        // 查询数据库
        List<Basket> baskets = basketMapper.selectBatchIds(basketIds);
        // 这里只有数量  你可以在cart-service  也可以在produict-service里面算
        List<Long> skuIds = baskets.stream()
                .map(Basket::getSkuId)
                .collect(Collectors.toList());
        List<Sku> skuList = basketSkuFeign.getSkuByIds(skuIds);

        // 真实业务 你查询用户有没有可用的优惠券  如果有 计算金额要算上
        List<BigDecimal> allMoney = new ArrayList<>();

        baskets.forEach(basket -> {
            Integer count = basket.getBasketCount();
            Sku sku1 = skuList.stream()
                    .filter(sku -> sku.getSkuId().equals(basket.getSkuId()))
                    .collect(Collectors.toList())
                    .get(0);
            BigDecimal price = sku1.getPrice();
            // 做乘法 count * price
            BigDecimal oneMoney = price.multiply(new BigDecimal(count));
            allMoney.add(oneMoney);
        });
        // 总计算
        BigDecimal totalMoney = allMoney.stream().reduce(BigDecimal::add).get();

        cartMoney.setFinalMoney(totalMoney);
        cartMoney.setTotalMoney(totalMoney);
        return cartMoney;
    }

    /**
     * 远程调用根据用户id和skuIds清空购物车
     *
     * @param openId
     * @param skuIds
     */
    @Override
    public void clearCart(String openId, List<Long> skuIds) {
        basketMapper.delete(new LambdaQueryWrapper<Basket>()
                .eq(Basket::getUserId, openId)
                .in(Basket::getSkuId, skuIds)
        );
    }
}
