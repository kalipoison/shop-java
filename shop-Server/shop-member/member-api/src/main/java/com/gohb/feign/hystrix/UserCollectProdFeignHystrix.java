package com.gohb.feign.hystrix;

import com.gohb.es.ProdEs;
import com.gohb.feign.UserCollectProdFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class UserCollectProdFeignHystrix implements UserCollectProdFeign {
    /**
     * 根据ids查询商品信息
     *
     * @param prodIds
     * @return
     */
    @Override
    public List<ProdEs> findProdEsByIds(List<Long> prodIds) {
        log.error("远程根据ids查询商品信息失败{}", prodIds);
        return null;
    }
}
