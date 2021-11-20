package com.gohb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.UserAddrOrder;
import com.gohb.mapper.UserAddrOrderMapper;
import com.gohb.service.UserAddrOrderService;
import org.springframework.stereotype.Service;


@Service
public class UserAddrOrderServiceImpl extends ServiceImpl<UserAddrOrderMapper, UserAddrOrder> implements UserAddrOrderService{

}
