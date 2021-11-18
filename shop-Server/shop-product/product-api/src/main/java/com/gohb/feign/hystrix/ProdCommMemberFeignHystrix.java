package com.gohb.feign.hystrix;

import com.gohb.domain.User;
import com.gohb.feign.ProdCommMemberFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProdCommMemberFeignHystrix implements ProdCommMemberFeign {
    /**
     * 远程调用获取用户信息集合
     *
     * @param userIds
     * @return
     */
    @Override
    public List<User> findUserInfoByUserIds(List<String> userIds) {
        log.error("远程调用获取用户信息集合失败");
        return null;
    }
}
