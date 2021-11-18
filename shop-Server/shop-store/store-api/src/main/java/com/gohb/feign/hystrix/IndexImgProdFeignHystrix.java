package com.gohb.feign.hystrix;

import com.gohb.feign.IndexImgProdFeign;
import com.gohb.domain.Prod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class IndexImgProdFeignHystrix implements IndexImgProdFeign {
    /**
     * 远程调用根据id查询商品信息
     *
     * @param prodId
     * @return
     */
    @Override
    public Prod findProdById(Long prodId) {
        log.error("远程调用失败了");
        return null;
    }
}