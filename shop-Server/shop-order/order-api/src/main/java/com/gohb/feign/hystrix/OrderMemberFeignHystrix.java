package com.gohb.feign.hystrix;

import com.gohb.domain.UserAddr;
import com.gohb.feign.OrderMemberFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class OrderMemberFeignHystrix implements OrderMemberFeign {
    /**
     * 查询用户的默认收货地址
     *
     * @param openId
     * @return
     */
    @Override
    public UserAddr getDefaultAddr(String openId) {
        log.error("远程调用查询用户的默认收货地址 失败");
        return null;
    }
}
