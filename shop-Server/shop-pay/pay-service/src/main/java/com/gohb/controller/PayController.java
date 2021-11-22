package com.gohb.controller;

import com.gohb.domain.OrderSettlement;
import com.gohb.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@Api("处理支付的接口")
@Slf4j
public class PayController {

    @Autowired
    private PayService payService;

    @PostMapping("p/order/pay")
    @ApiOperation("生成支付二维码的接口")
    public ResponseEntity<String> createPayQrCode(@RequestBody OrderSettlement orderSettlement) {
        String qrCode = payService.pay(orderSettlement);
        return ResponseEntity.ok(qrCode);
    }

    /**
     * 支付宝不能直接调用我们这个接口 因为支付宝没有token
     * 因为我们pay-service是一个受保护的服务
     *
     * @param map
     * @return
     */
    @PostMapping("/payNotify")
    @ApiOperation("处理支付宝回调的接口")
    public ResponseEntity<String> payNotify(@RequestParam Map<String, String> map) {
        String trade_status = map.get("trade_status");
        if ("TRADE_SUCCESS".equals(trade_status)) {
            // 说明支付成功了
            // 验证是否是支付宝调用我的
            Boolean flag = payService.checkRsa2(map);
            // 如果是支付宝调用我的  我们就去修改数据库的订单状态
            if (flag) {
                String orderNum = map.get("out_trade_no");
                payService.paySuccessChangeStatus(orderNum);
                return ResponseEntity.ok("回调成功");
            }
            // 失败了
            log.error("你小心点，你是黑客");
        }
        return ResponseEntity.ok("ok");
    }


}