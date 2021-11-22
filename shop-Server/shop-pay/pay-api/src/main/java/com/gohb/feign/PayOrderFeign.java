package com.gohb.feign;

import com.gohb.domain.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "order-service")
public interface PayOrderFeign {

    /**
     * 远程调用 根据订单号 拿到订单对象
     *
     * @param orderNum
     * @return
     */
    @PostMapping("getOrderByOrderNum")
    Order getOrderByOrderNum(@RequestParam String orderNum);

    /**
     * 修改订单状态
     *
     * @param orderNum
     */
    @PostMapping("/changeOrderStatus")
    void changeOrderStatus(@RequestParam String orderNum);

}
