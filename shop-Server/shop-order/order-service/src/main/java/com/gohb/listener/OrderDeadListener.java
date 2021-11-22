package com.gohb.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import com.gohb.constant.QueueConstant;
import com.gohb.domain.Order;
import com.gohb.domain.OrderItem;
import com.gohb.feign.OrderProductFeign;
import com.gohb.service.OrderItemService;
import com.gohb.service.OrderService;
import com.gohb.service.OrderSettlementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class OrderDeadListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderProductFeign orderProductFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderSettlementService orderSettlementService;

    /**
     * 处理订单超时未支付的监听
     *
     * @param message
     * @param channel
     */
    @RabbitListener(queues = QueueConstant.ORDER_DEAD_QUEUE, concurrency = "3-5")
    public void orderDeadHandler(Message message, Channel channel) {
        String orderNum = new String(message.getBody());
        // 查询数据库 order表   这边查到的是 1 未支付   在下面判断的时候 支付那边一瞬间就修改了 出现了 用户已经支付了 但是订单回滚了
        // 这里可以考虑 分布式锁的问题 redis redission 加一个分布式锁  在pay-service修改订单状态的时候 也要用同一把锁  mysql乐观锁
        Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNumber, orderNum)
        );
        if (ObjectUtils.isEmpty(order)) {
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        // 签收消息
        try {
            realOrderHandler(message, channel, orderNum, order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("延迟订单处理失败，可以重试，也可以记录数据库");
        }
    }

    /**
     * 真正处理订单回滚的方法
     *
     * @param message
     * @param channel
     * @param orderNum
     * @param order
     */
    private void realOrderHandler(Message message, Channel channel, String orderNum, Order order) {
        Integer status = order.getStatus();
        if (status != 1 && status != 6) {
            // 已经支付了 签收这个消息 直接返回了
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        // 订单 有问题
        log.error("订单{}出现问题", orderNum);
        // 1. 做数据mysql的回滚  包括es的回滚  还可以给用户发一个消息
        // 又要查询出来 prod  sku的有几个
        List<OrderItem> orderItemList = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderNumber, orderNum)
        );
        if (CollectionUtils.isEmpty(orderItemList)) {
            log.error("订单{}不存在", orderNum);
            return;
        }
        // 组装数据 map<String,Map<Long,Integer>>
        HashMap<String, Map<Long, Integer>> data = new HashMap<>(4);
        Map<Long, Integer> prodMap = new HashMap<>();
        Map<Long, Integer> skuMap = new HashMap<>();
        orderItemList.forEach(orderItem -> {
            Long prodId = orderItem.getProdId();
            Long skuId = orderItem.getSkuId();
            Integer count = orderItem.getProdCount();
            if (prodMap.containsKey(prodId)) {
                prodMap.put(prodId, prodMap.get(prodId) + count);
            } else {
                prodMap.put(prodId, count);
            }
            skuMap.put(skuId, count);
        });
        data.put("prod", prodMap);
        data.put("sku", skuMap);
        // 做远程调用了 回滚库存 在mq里面发一个远程调用 需要访问受保护的资源 他就要token 我们给一个永久token
        orderProductFeign.changeStock(data);
        // 回滚es的库存
        rabbitTemplate.convertAndSend(QueueConstant.PROD_CHANGE_EX, QueueConstant.PROD_CHANGE_KEY, JSON.toJSONString(prodMap));
        // 2. 修改订单的状态状
        order.setStatus(6);
        order.setCancelTime(new Date());
        order.setCloseType(1);
        order.setUpdateTime(new Date());
        orderService.updateById(order);
    }


}
