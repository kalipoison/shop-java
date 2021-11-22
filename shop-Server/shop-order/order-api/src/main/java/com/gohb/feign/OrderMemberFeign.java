package com.gohb.feign;

import com.gohb.domain.UserAddr;
import com.gohb.feign.hystrix.OrderMemberFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "member-service", fallback = OrderMemberFeignHystrix.class)
public interface OrderMemberFeign {

    /**
     * 查询用户的默认收货地址
     *
     * @param openId
     * @return
     */
    @GetMapping("p/address/getDefaultAddr")
    UserAddr getDefaultAddr(@RequestParam("openId") String openId);


}