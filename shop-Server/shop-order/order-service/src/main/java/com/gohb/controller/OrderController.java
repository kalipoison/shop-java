package com.gohb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.Order;
import com.gohb.service.OrderService;
import com.gohb.vo.OrderConfirmResult;
import com.gohb.vo.OrderParam;
import com.gohb.vo.OrderStatusResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@Api("订单管理接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/p/myOrder/orderCount")
    @ApiOperation("查询当前用户的订单数量")
    public ResponseEntity<OrderStatusResult> findOrderCount() {
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        OrderStatusResult orderStatusResult = orderService.findOrderStatus(openId);
        return ResponseEntity.ok(orderStatusResult);
    }

    @GetMapping("/p/myOrder/myOrder")
    @ApiOperation("分页查询当前用户的订单")
    public ResponseEntity<Page<Order>> myOrderPage(Page<Order> page, Order order) {
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        order.setUserId(openId);
        Page<Order> orderPage = orderService.findOrderPage(page, order);
        return ResponseEntity.ok(orderPage);
    }

    /**
     * controller写接口信息(参数校验) 不写业务
     * service 只写业务
     *
     * @param orderParam
     * @return
     */
    @PostMapping("/p/order/confirm")
    @ApiOperation("订单的确认接口")
    public ResponseEntity<OrderConfirmResult> myOrderPage(@RequestBody OrderParam orderParam) {
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        OrderConfirmResult orderConfirmResult = orderService.confirm(openId, orderParam);
        return ResponseEntity.ok(orderConfirmResult);
    }


    /**
     * 返回的订单号 我们是string 但是 ajax做数据处理的时候 会把string 转换成大的正型  就会出现精度确实
     * 返回的时候 不要返回纯数字
     *
     * @param orderConfirmResult
     * @return
     */
    @PostMapping("/p/order/submit")
    @ApiOperation("订单的提交")
    public ResponseEntity<String> myOrderSubmit(@RequestBody OrderConfirmResult orderConfirmResult) {
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        String orderNum = orderService.submit(openId, orderConfirmResult);
        return ResponseEntity.ok("orderNum:" + orderNum);
    }


    @GetMapping("/p/order/query")
    @ApiOperation("订单的支付状态查询")
    public ResponseEntity<String> queryOrderStatus(String orderSn) {
        Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNumber, orderSn)
                .eq(Order::getIsPayed, 1)
        );
        return ResponseEntity.ok(order != null ? "ok" : "fail");
    }


    /**
     * 远程调用 根据订单号 拿到订单对象
     *
     * @param orderNum
     * @return
     */
    @PostMapping("getOrderByOrderNum")
    @ApiOperation("根据订单号拿到订单对象")
    Order getOrderByOrderNum(@RequestParam String orderNum) {
        return orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNumber, orderNum)
        );
    }


    /**
     * 修改订单状态 为已经支付
     *
     * @param orderNum
     */
    @PostMapping("/changeOrderStatus")
    @ApiOperation("修改订单状态为已经支付")
    void changeOrderStatus(@RequestParam String orderNum) {
        orderService.changeOrderIsPay(orderNum);
    }


}