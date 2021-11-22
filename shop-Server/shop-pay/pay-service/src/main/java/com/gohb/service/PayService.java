package com.gohb.service;

import com.gohb.domain.OrderSettlement;

import java.util.Map;


public interface PayService {


    /**
     * 调用支付生成二维码的接口
     *
     * @return
     */
    String pay(OrderSettlement orderSettlement);


    /**
     * 验证签名是否正确
     *
     * @param map
     * @return
     */
    Boolean checkRsa2(Map<String, String> map);

    /**
     * 支付成功 修改订单状态的接口
     *
     * @param orderNum
     */
    void paySuccessChangeStatus(String orderNum);


}
