package com.gohb.controller;

import com.gohb.domain.User;
import com.gohb.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "会员管理接口/前台用户管理接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/p/user/setUserInfo")
    @ApiOperation("设置用户信息")
    public ResponseEntity<Void> setUserInfo(@RequestBody @Validated User user) {
        //拿到user
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        user.setUserId(openId);
        userService.updateById(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("getUserInfoById")
    @ApiOperation("获取用户的信息")
    public User getUserInfo(String openId) {
        return userService.getById(openId);
    }

}
