package com.gohb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.OrderRefund;
import com.gohb.mapper.OrderRefundMapper;
import com.gohb.service.OrderRefundService;
import org.springframework.stereotype.Service;

@Service
public class OrderRefundServiceImpl extends ServiceImpl<OrderRefundMapper, OrderRefund> implements OrderRefundService{

}
