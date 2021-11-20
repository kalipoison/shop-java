package com.gohb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.UserAddr;

import java.util.List;

public interface UserAddrService extends IService<UserAddr> {


    /**
     * 全查询用户的收货地址
     *
     * @param openId
     * @return
     */
    List<UserAddr> findUserAddr(String openId);

    /**
     * 修改默认收货地址
     *
     * @param openId
     * @param id
     */
    void changeUserDefaultAddr(String openId, Long id);


    /**
     * 设置用户的默认的收货地址
     *
     * @param openId
     * @return
     */
    UserAddr getUserDefaultAddr(String openId);
}
