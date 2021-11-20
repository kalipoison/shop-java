package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.constant.QueueConstant;
import com.gohb.domain.User;
import com.gohb.mapper.UserMapper2;
import com.gohb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper2, User> implements UserService {

    @Autowired
    private UserMapper2 userMapper2;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public boolean updateById(User user) {
        user.setModifyTime(new Date());
        user.setUserLasttime(new Date());
        int i = userMapper2.updateById(user);
        return i > 0;
    }


//    /**
//     * 发注册验证码
//     * 1. 生成一个code
//     * 2. 放在redis
//     * 3. 设置一个过期时间 5min
//     * 4. 组装参数
//     * 5. 放入mq
//     * 6. 返回
//     *
//     * @param openId
//     * @param sendMap
//     */
//    @Override
//    public void sendMsg(String openId, Map<String, String> sendMap) {
//        String phonenum = sendMap.get("phonenum").toString();
//        // 生成一个验证码
//        String code = createCode();
//        // 放redis
//        redisTemplate.opsForValue().set(phonenum, code, Duration.ofMinutes(5));
//        // 组装参数 放mq 短信的签名 短信的模板，短信的code
//        AliSmsModel aliSmsModel = new AliSmsModel();
//        aliSmsModel.setPhoneNumbers(phonenum);
//        aliSmsModel.setSignName("ego商城");
//        aliSmsModel.setTemplateCode("SMS_203185255");
//        HashMap<String, String> map = new HashMap<>(2);
//        map.put("code", code);
//        aliSmsModel.setTemplateParam(JSON.toJSONString(map));
//        // 放mq
//        rabbitTemplate.convertAndSend(QueueConstant.PHONE_SEND_EX, QueueConstant.PHONE_SEND_KEY, JSON.toJSONString(aliSmsModel));
//    }

    private String createCode() {
        // 位数 4  6  8多一点
        // 随机生成验证码
        return "888888";
    }

    /**
     * 保存手机号
     * 1. 判断redis有没有key
     *
     * @param openId
     * @param sendMap
     */
    @Override
    public void savePhone(String openId, Map<String, String> sendMap) {
        String phonenum = sendMap.get("phonenum").toString();
        String redisCode = redisTemplate.opsForValue().get(phonenum);
        // 判断前台穿不进来的和redis 是否一致
        String code = sendMap.get("code");
        if (!StringUtils.isEmpty(redisCode) && code.equals(redisCode)) {
            // 说明是对的 记录数据库
            User user = new User();
            user.setUserMobile(phonenum);
            userMapper2.update(user, new LambdaQueryWrapper<User>()
                    .eq(User::getUserId, openId)
            );
            redisTemplate.delete(phonenum);
        }

    }
}
