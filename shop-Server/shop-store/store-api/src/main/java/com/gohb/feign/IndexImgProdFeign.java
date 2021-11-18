package com.gohb.feign;

import com.gohb.domain.Prod;
import com.gohb.feign.hystrix.IndexImgProdFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "product-service", fallback = IndexImgProdFeignHystrix.class)
public interface IndexImgProdFeign {

//    /**
//     * 远程调用根据id查询商品信息
//     *
//     * @param prodId
//     * @return
//     */
//    @GetMapping("/prod/prod/findProdById")
//    Prod findProdById(@RequestParam("prodId") Long prodId);

    /**
     * 查询单个商品
     *
     * @param id
     * @return
     */
    @GetMapping("/prod/prod/info/{id}")
    Prod findProdById(@PathVariable("id") Long id);

}