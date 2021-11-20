package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.User;

import java.util.Map;


public interface UserService extends IService<User> {


//    /**
//     * 发注册验证码
//     *
//     * @param openId
//     * @param sendMap
//     */
//    void sendMsg(String openId, Map<String, String> sendMap);

    /**
     * 保存手机号
     *
     * @param openId
     * @param sendMap
     */
    void savePhone(String openId, Map<String, String> sendMap);


}
