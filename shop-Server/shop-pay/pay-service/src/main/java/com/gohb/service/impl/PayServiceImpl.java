package com.gohb.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.gohb.config.AliPayProperties;
import com.gohb.domain.Order;
import com.gohb.domain.OrderSettlement;
import com.gohb.feign.PayOrderFeign;
import com.gohb.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Map;


@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    private PayOrderFeign payOrderFeign;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AliPayProperties aliPayProperties;


    @Autowired
    private AlipayTradeService alipayTradeService;

    /**
     * 调用支付生成二维码的接口
     * 1. 拿到订单号 去查询我们的订单 拿到订名称 订单金额
     * 2. 组装ali预支付的参数
     * 3. 调用阿里预支付
     * 4. 拿到预支付结果 也就是二维码
     * 5. 返回二维码
     *
     * @param orderSettlement
     * @return
     */
    @Override
    public String pay(OrderSettlement orderSettlement) {
        Order order = payOrderFeign.getOrderByOrderNum(orderSettlement.getOrderNumber());
        if (ObjectUtils.isEmpty(order)) {
            throw new RuntimeException("服务器维护中");
        }
        String prodName = order.getProdName();
        BigDecimal actualTotal = order.getActualTotal();
        // 组装一个支付预订单的对象

        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject("ego商城订单")
                .setTotalAmount(actualTotal.toString())
                .setOutTradeNo(orderSettlement.getOrderNumber())
                .setUndiscountableAmount("0")
                .setSellerId("2088102177336125")
                .setBody(prodName)
                .setOperatorId("test_operator_id")
                .setStoreId("test_store_id")
                .setTimeoutExpress("120m")
                .setNotifyUrl(aliPayProperties.getNotifyUrl());//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
        // 生成预订单
        AlipayF2FPrecreateResult result = alipayTradeService.tradePrecreate(builder);
        TradeStatus tradeStatus = result.getTradeStatus();
        switch (tradeStatus) {
            case SUCCESS:
                // 拿到二维码
                String qrCode = result.getResponse().getQrCode();
                System.out.println(qrCode);
                return qrCode;
            case UNKNOWN:
                throw new RuntimeException("创建支付订单失败");
            case FAILED:
                throw new RuntimeException("创建支付订单失败");
            default:
                throw new RuntimeException("创建支付订单失败");
        }
    }

    /**
     * 验证签名是否正确
     *
     * @param map
     * @return
     */
    @Override
    public Boolean checkRsa2(Map<String, String> map) {
        Boolean flag = true;
        try {
            flag = AlipaySignature.rsaCheckV1(map,
                    aliPayProperties.getAlipayPublicKey(),
                    "UTF-8",
                    aliPayProperties.getSignType()
            );
        } catch (AlipayApiException e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 支付成功 修改订单状态的接口
     *
     * @param orderNum
     */
    @Override
    public void paySuccessChangeStatus(String orderNum) {
        // 远程调用 修改订单状态了
        payOrderFeign.changeOrderStatus(orderNum);
    }
}
