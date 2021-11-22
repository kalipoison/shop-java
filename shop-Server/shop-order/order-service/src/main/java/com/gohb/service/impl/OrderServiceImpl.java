package com.gohb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.constant.QueueConstant;
import com.gohb.domain.*;
import com.gohb.feign.OrderCartFeign;
import com.gohb.feign.OrderMemberFeign;
import com.gohb.feign.OrderProductFeign;
import com.gohb.mapper.OrderItemMapper;
import com.gohb.mapper.OrderMapper;
import com.gohb.mapper.OrderSettlementMapper;
import com.gohb.model.WxMsgModel;
import com.gohb.service.OrderItemService;
import com.gohb.service.OrderService;
import com.gohb.utils.SnowUtil;
import com.gohb.vo.OrderConfirmResult;
import com.gohb.vo.OrderParam;
import com.gohb.vo.OrderStatusResult;
import com.gohb.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMemberFeign orderMemberFeign;

    @Autowired
    private OrderProductFeign orderProductFeign;

    @Autowired
    private OrderCartFeign orderCartFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderSettlementMapper orderSettlementMapper;


    /**
     * 查询当前用户的订单数量
     *
     * @param openId
     * @return
     */
    @Override
    public OrderStatusResult findOrderStatus(String openId) {
        // 用户的订单也是比较多的
        Integer unPay = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, openId)
                .eq(Order::getStatus, 1)
        );
        Integer payed = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, openId)
                .eq(Order::getStatus, 2)
        );
        Integer consignment = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, openId)
                .eq(Order::getStatus, 3)
        );
        OrderStatusResult orderStatusResult = new OrderStatusResult();
        orderStatusResult.setPayed(payed);
        orderStatusResult.setUnPay(unPay);
        orderStatusResult.setConsignment(consignment);
        return orderStatusResult;
    }

    /**
     * 分页查询当前用户的订单
     *
     * @param page
     * @param order
     * @return
     */
    @Override
    public Page<Order> findOrderPage(Page<Order> page, Order order) {
        // 首先查询订单表  orderNum 再查询订单条目表 List<orderItem> 组装数据 返回
        Page<Order> orderPage = orderMapper.selectPage(page, new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, order.getUserId())
                .eq(order.getStatus() != 0, Order::getStatus, order.getStatus())
        );
        // 1 2
        List<Order> orderList = orderPage.getRecords();

        // 订单和条目表是一个 一对多的关系 orderNum
        List<String> orderNumList = orderList.stream()
                .map(Order::getOrderNumber)
                .collect(Collectors.toList());
        // 查询订单条目表 1 2 3 4
        List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .in(OrderItem::getOrderNumber, orderNumList)
        );
        // 循环 组装数据了
        orderList.forEach(order1 -> {
            List<OrderItem> orderItems = orderItemList.stream()
                    .filter(orderItem -> orderItem.getOrderNumber().equals(order1.getOrderNumber()))
                    .collect(Collectors.toList());
            order1.setOrderItemDtos(orderItems);
        });
        return orderPage;
    }


    /**
     * 订单的确认接口
     * 1. 用户的默认收货地址的查询
     * 2. 判断用户到底是从购物车进来下单 还是直接从商品进来下单
     * 3.
     *
     * @param openId
     * @param orderParam
     * @return
     */
    @Override
    public OrderConfirmResult confirm(String openId, OrderParam orderParam) {
        OrderConfirmResult orderConfirmResult = new OrderConfirmResult();
        // 1 先查询用户的默认收货地址
        UserAddr defaultAddr = orderMemberFeign.getDefaultAddr(openId);
        orderConfirmResult.setUserAddr(defaultAddr);
        List<Long> basketIds = orderParam.getBasketIds();
        if (CollectionUtils.isEmpty(basketIds)) {
            // 是从商品直接进来的 商品信息，用户id 一考虑到用户可能有优惠券
            orderConfirmResult = productToOrder(orderConfirmResult, orderParam, openId);
        } else {
            // 从购物车进来商品的确认
            orderConfirmResult = cartToOrder(orderConfirmResult, basketIds, openId);
        }
        return orderConfirmResult;
    }

    /**
     * 从购物车进来商品的确认
     * 1. 查询购物车的集合
     * 2. 拿到skuIds
     * 3. 远程调用商品 拿到sku集合
     * 4. 循环组装数据了
     *
     * @param orderConfirmResult
     * @param basketIds
     * @param openId
     * @return
     */
    private OrderConfirmResult cartToOrder(OrderConfirmResult orderConfirmResult, List<Long> basketIds, String openId) {
        // 根据购物车ids查询购物车集合
        List<Basket> basketList = orderCartFeign.getBasketByIds(basketIds);
        if (CollectionUtils.isEmpty(basketList)) {
            throw new RuntimeException("服务器维护中，请稍后再试");
        }
        // 拿到skuIds
        List<Long> skuIds = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());
        // 远程调用拿到sku的集合
        List<Sku> skuList = orderProductFeign.getSkuByIds(skuIds);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new RuntimeException("服务器维护中，请稍后再试");
        }
        // 创建订单条目集合
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        // 创建金额集合
        ArrayList<BigDecimal> totalMoneyList = new ArrayList<>();

        // 循环购物车集合 组装数据了
        basketList.forEach(basket -> {
            // 创建订单条目
            OrderItem orderItem = new OrderItem();
            // 筛选出匹配的sku
            Sku sku1 = skuList.stream()
                    .filter(sku -> sku.getSkuId().equals(basket.getSkuId()))
                    .collect(Collectors.toList())
                    .get(0);
            // 得到单个商品的单价
            BigDecimal price = sku1.getPrice();
            // 得到这个商品的数量
            Integer basketCount = basket.getBasketCount();
            // 这是一个商品的总金额
            BigDecimal oneMoney = price.multiply(new BigDecimal(basketCount));
            // 放到金额池中
            totalMoneyList.add(oneMoney);
            // 对象属性拷贝
            BeanUtil.copyProperties(sku1, orderItem, true);
            // 有一些名字没有匹配上 我们就需要手动设置
            orderItem.setProdCount(basketCount);
            // 将订单条目对象放入订单条目集合中
            orderItems.add(orderItem);
        });
        // 创建订单详情集合
        ArrayList<OrderVo> orderVos = new ArrayList<>();
        // 创建订单详情
        OrderVo orderVo = new OrderVo();
        // 订单详情 设置订单条目集合
        orderVo.setShopCartItemDiscounts(orderItems);
        orderVos.add(orderVo);
        orderConfirmResult.setShopCartOrders(orderVos);
        // 设置商品总个数
        Integer totalCount = basketList.stream().map(Basket::getBasketCount).reduce(Integer::sum).get();
        orderConfirmResult.setTotalCount(totalCount);
        // 计算总金额
        BigDecimal totalMoney = totalMoneyList.stream().reduce(BigDecimal::add).get();
        orderConfirmResult.setTotal(totalMoney);
        orderConfirmResult.setActualTotal(totalMoney);
        return orderConfirmResult;
    }

    /**
     * 是从商品直接进来的订单确认
     * 1. 直接拿到商品的id
     * 2. 远程调用查询商品的sku
     * 3. 组装数据
     *
     * @param orderConfirmResult
     * @param orderParam
     * @param openId
     * @return
     */
    private OrderConfirmResult productToOrder(OrderConfirmResult orderConfirmResult, OrderParam orderParam, String openId) {
        OrderItem orderItem = orderParam.getOrderItem();
        // 远程调用查询商品的信息 计算价格 price * count
        Long skuId = orderItem.getSkuId();
        Integer prodCount = orderItem.getProdCount();
        // 远程调用 计算价格 封装对象
        List<Sku> skuList = orderProductFeign.getSkuByIds(Arrays.asList(skuId));
        if (CollectionUtils.isEmpty(skuList)) {
            // 商品的核心数据都没有 就不需要往下走了
            throw new RuntimeException("服务器维护中，请稍后再试");
        }
        Sku sku = skuList.get(0);
        // 计算价格 设置信息 名称 图片 数量
        orderConfirmResult.setTotalCount(prodCount);
        // 计算金额
        BigDecimal total = sku.getPrice().multiply(new BigDecimal(prodCount));
        orderConfirmResult.setYunfei(new BigDecimal(6));
        // 比如运费就是6块
        orderConfirmResult.setActualTotal(total.add(new BigDecimal(6)));
        orderConfirmResult.setTotal(total);
        ArrayList<OrderVo> orderVos = new ArrayList<>();
        OrderVo orderVo = new OrderVo();
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        // 每个的运费
        orderVo.setTransfee(new BigDecimal(6));
        // orderItem设置值
        orderItem.setPrice(sku.getPrice());
        orderItem.setPic(sku.getPic());
        orderItem.setSkuName(sku.getSkuName());
        orderItem.setProdName(sku.getProdName());
        orderItem.setSkuId(sku.getSkuId());
        orderItem.setProdId(sku.getProdId());
        orderItem.setShopId(1L);
        orderItems.add(orderItem);
        orderVo.setShopCartItemDiscounts(orderItems);
        orderVos.add(orderVo);
        // 设置商品信息
        orderConfirmResult.setShopCartOrders(orderVos);
        return orderConfirmResult;
    }


    /**
     * 订单的提交
     * 1. 清空购物车
     * 2. 扣减mysql的库存
     * 3. 扣减es的库存
     * 4. 生成一个订单号
     * 5. 写订单表
     * 6. 写延迟队列
     * 7. 发微信消息通知用户来支付
     *
     * @param openId
     * @param orderConfirmResult
     * @return
     */
    @Override
    @Transactional
    public String submit(String openId, OrderConfirmResult orderConfirmResult) {

        // 不能把购物车直接干掉 能从商品来下订单
        clearCart(openId, orderConfirmResult);

        // 扣减mysql的库存  扣减mysql  prod  18 88    sku  888 889 990 991 表
        Map<Long, Integer> map = changeStock(orderConfirmResult);

        // 扣减es的库存
        changeEsStock(map);

        // 生成一个订单号
        String orderNum = createOrderNum();

        // 写订单表
        Map<String, Object> orderDetails = writeOrder(orderNum, openId, orderConfirmResult);

        // 写延迟队列
        writeDeadQueue(orderNum);

        // 发微信消息 给哪个用户 发商品名称 订单 发 金额
        sendWxMsg(orderDetails, openId, orderNum);

        return orderNum;
    }

    /**
     * 发送微信消息的方法
     *
     * @param orderDetails
     * @param openId
     * @param orderNum
     */
    private void sendWxMsg(Map<String, Object> orderDetails, String openId, String orderNum) {
        WxMsgModel wxMsgModel = new WxMsgModel();
        wxMsgModel.setToUser("oy_5Lv95ANQGqolUcwyRfNI_1BOQ");
        wxMsgModel.setTopColor("#173177");
        wxMsgModel.setTemplateId("4X8kV75LFlVs_T1WOKCEPHSB-DFDkJ_ePEJCn3TuIUw");
        wxMsgModel.setUrl("https://www.baidu.com");
        HashMap<String, Map<String, String>> data = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        data.put("time", WxMsgModel.buildMap(time, "#173177"));
        data.put("prodName", WxMsgModel.buildMap(orderDetails.get("prodName").toString(), "#173177"));
        data.put("price", WxMsgModel.buildMap(orderDetails.get("price").toString(), "#173177"));
        data.put("orderNum", WxMsgModel.buildMap(orderNum, "#173177"));
        wxMsgModel.setData(data);
        // 放mq 发消息
        rabbitTemplate.convertAndSend(QueueConstant.WECHAT_SEND_EX, QueueConstant.WECHAT_SEND_KEY, JSON.toJSONString(wxMsgModel));
    }

    /**
     * 写延迟队列
     *
     * @param orderNum
     */
    private void writeDeadQueue(String orderNum) {
        rabbitTemplate.convertAndSend(QueueConstant.ORDER_MS_QUEUE, orderNum);
    }

    /**
     * 写订单表
     * 写order表 需要 一个订单名称  要注意金额 我们直接用前台传进来的金额数值， 我们需要重新计算金额
     * 写orderItem表
     * 写orderSettlement
     *
     * @param orderNum
     * @param openId
     * @param orderConfirmResult
     */
    private Map<String, Object> writeOrder(String orderNum, String openId, OrderConfirmResult orderConfirmResult) {

        Map<String, Object> map = new HashMap<>();

        // 1. 先写订单条目表 skuId  count  价格不能直接用
        List<OrderItem> shopCartItemDiscounts = orderConfirmResult.getShopCartOrders().get(0).getShopCartItemDiscounts();
        // 2. 拿到skuIds
        List<Long> skuIds = shopCartItemDiscounts.stream()
                .map(OrderItem::getSkuId)
                .collect(Collectors.toList());
        // 3. 远程调用查询sku的集合
        List<Sku> skuList = orderProductFeign.getSkuByIds(skuIds);
        // 存放所有单品金额的集合
        List<BigDecimal> totalMoneyList = new ArrayList<>();

        // 收集名字  StringBuffer 是安全的
        StringBuffer sb = new StringBuffer();

        // 4. 循环计算每一个商品的总价  还需要计算所有商品的总价
        shopCartItemDiscounts.forEach(orderItem -> {
            // 计算商品的单个总价
            Integer count = orderItem.getProdCount();
            // 过滤条件 找到对应的sku
            Sku sku1 = skuList.stream()
                    .filter(sku -> sku.getSkuId().equals(orderItem.getSkuId()))
                    .collect(Collectors.toList())
                    .get(0);
            // 拿到单价
            BigDecimal price = sku1.getPrice();
            // 计算单品金额
            BigDecimal oneMoney = price.multiply(new BigDecimal(count));
            // 收集单品金额
            totalMoneyList.add(oneMoney);
            // 这个是商品的单价
            orderItem.setPrice(price);
            orderItem.setProductTotalAmount(oneMoney);
            orderItem.setUserId(openId);
            orderItem.setOrderNumber(orderNum);
            orderItem.setRecTime(new Date());
            // 收集名字
            sb.append(orderItem.getProdName());
            sb.append(",");
        });
        // 统一插入数据库 不在循环里面操作数据库
        orderItemService.saveBatch(shopCartItemDiscounts);

        // 写order表了
        Order order = new Order();
        order.setOrderNumber(orderNum);
        order.setUserId(openId);
        // 总金额
        BigDecimal totalMoney = totalMoneyList.stream().reduce(BigDecimal::add).get();
        order.setActualTotal(totalMoney);
        order.setTotal(totalMoney);
        order.setRemarks("这是一个测试订单");
        // 计算有多少个商品
        Integer prodNums = shopCartItemDiscounts.stream().map(OrderItem::getProdCount).reduce(Integer::sum).get();
        order.setProductNums(prodNums);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setIsPayed(0);
        order.setShopId(1L);
        order.setStatus(1);
        String prodNameStr = sb.toString();
        // 苹果手机，华为，
        String finalName = prodNameStr.substring(0, prodNameStr.lastIndexOf(","));
        order.setProdName(finalName);
        // 插入数据库
        orderMapper.insert(order);

        // 写订单结算表 预结算
        OrderSettlement orderSettlement = new OrderSettlement();
        orderSettlement.setCreateTime(new Date());
        orderSettlement.setOrderNumber(orderNum);
        orderSettlement.setUserId(openId);
        orderSettlement.setPayStatus(0);
        orderSettlement.setVersion(0);
        orderSettlement.setIsClearing(0);
        orderSettlementMapper.insert(orderSettlement);

        map.put("price", totalMoney);
        map.put("prodName", finalName);
        return map;
    }


    /**
     * 给这个订单生成一个订单号
     *
     * @return
     */
    private String createOrderNum() {
//        long l = System.currentTimeMillis();
        Snowflake snowflake = SnowUtil.snowflake;
        String orderNum = snowflake.nextIdStr();
        return orderNum;
    }

    /**
     * 扣减es的库存
     * 异步的方式 mq 就是商品的快速导入
     *
     * @param map
     */
    private void changeEsStock(Map<Long, Integer> map) {
        rabbitTemplate.convertAndSend(QueueConstant.PROD_CHANGE_EX, QueueConstant.PROD_CHANGE_KEY, JSON.toJSONString(map));
    }

    /**
     * 扣减mysql的库存
     * 扣减两个表  如果用户没支付 库存的回滚 又要加回来吧
     * prod  18
     * sku 888 2个 889 2个
     *
     * @param orderConfirmResult
     * @return
     */
    private Map<Long, Integer> changeStock(OrderConfirmResult orderConfirmResult) {
        // 远程调用扣减库存   Map<"prod",Map<Long,Integer>>  Map<"sku",Map<Long,Integer>>
        HashMap<String, Map<Long, Integer>> data = new HashMap<>(4);
        // prod的map
        HashMap<Long, Integer> prodMap = new HashMap<>();
        HashMap<Long, Integer> skuMap = new HashMap<>();

        // 组装数据了
        List<OrderItem> shopCartItemDiscounts = orderConfirmResult.getShopCartOrders().get(0).getShopCartItemDiscounts();
        shopCartItemDiscounts.forEach(orderItem -> {
            Long prodId = orderItem.getProdId();
            Long skuId = orderItem.getSkuId();
            // 是因为我考虑到回滚的问题 下单要减库存  回滚要加库存  所以用+ - 来判断  最终的操作数据库的方法 +
            Integer prodCount = orderItem.getProdCount() * -1;

            if (prodMap.containsKey(prodId)) {
                // 包含 做累加
                prodMap.put(prodId, prodMap.get(prodId) + prodCount);
            } else {
                prodMap.put(prodId, prodCount);  // 2 + 2 =  4
            }
            skuMap.put(skuId, prodCount);
        });
        data.put("prod", prodMap);
        data.put("sku", skuMap);
        // 把 data这个大map 当参数 发给product-service
        orderProductFeign.changeStock(data);

        return prodMap;
    }

    /**
     * 清空购物车方法
     * 需要根据用户id和skuId唯一确定一行数据 然后删掉
     *
     * @param openId
     * @param orderConfirmResult
     */
    private void clearCart(String openId, OrderConfirmResult orderConfirmResult) {
        List<OrderVo> shopCartOrders = orderConfirmResult.getShopCartOrders();
        OrderVo orderVo = shopCartOrders.get(0);
        List<OrderItem> shopCartItemDiscounts = orderVo.getShopCartItemDiscounts();
        // 拿到skuIds 远程调用清空购物车
        List<Long> skuIds = shopCartItemDiscounts.stream()
                .map(OrderItem::getSkuId)
                .collect(Collectors.toList());
        // 远程调用清空购物车
        orderCartFeign.clearCart(openId, skuIds);
    }

    /**
     * 修改订单状态为已经支付
     *
     * @param orderNum
     */
    @Override
    public void changeOrderIsPay(String orderNum) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNumber, orderNum)
        );
        if (!ObjectUtils.isEmpty(order)) {
            order.setStatus(2);
            order.setIsPayed(1);
            order.setPayTime(new Date());
            order.setUpdateTime(new Date());
            orderMapper.updateById(order);
        }
        // 更新订单结算表
        OrderSettlement orderSettlement = orderSettlementMapper.selectOne(new LambdaQueryWrapper<OrderSettlement>()
                .eq(OrderSettlement::getOrderNumber, orderNum)
        );
        if (!ObjectUtils.isEmpty(orderSettlement)) {
            orderSettlement.setClearingTime(new Date());
            orderSettlement.setPayStatus(1);
            orderSettlement.setIsClearing(1);
            orderSettlementMapper.updateById(orderSettlement);
        }
    }
}
