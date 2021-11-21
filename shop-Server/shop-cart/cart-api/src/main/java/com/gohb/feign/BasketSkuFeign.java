package com.gohb.feign;

import com.gohb.domain.Sku;
import com.gohb.feign.hystrix.BasketSkuFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(value = "product-service", fallback = BasketSkuFeignHystrix.class)
public interface BasketSkuFeign {

    /**
     * 远程调用根据skuIds查询sku的集合
     *
     * @param skuIds
     * @return
     */
    @PostMapping("/prod/prod/getSkuByIds")
    List<Sku> getSkuByIds(@RequestBody List<Long> skuIds);

}
