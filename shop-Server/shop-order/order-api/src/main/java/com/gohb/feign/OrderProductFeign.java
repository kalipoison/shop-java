package com.gohb.feign;

import com.gohb.domain.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


@FeignClient(value = "product-service"/*, fallback = OrderProductFeignHystrix.class*/)
public interface OrderProductFeign {


    /**
     * 远程调用根据skuIds查询sku的集合
     *
     * @param skuIds
     * @return
     */
    @PostMapping("/prod/prod/getSkuByIds")
    List<Sku> getSkuByIds(@RequestBody List<Long> skuIds);

    /**
     * 修改库存的方法
     *
     * @param stockMap
     */
    @PostMapping("/prod/prod/changeStock")
    void changeStock(@RequestBody Map<String, Map<Long, Integer>> stockMap);


}
