package com.gohb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.OrderItem;
import com.gohb.mapper.OrderItemMapper;
import com.gohb.service.OrderItemService;
import org.springframework.stereotype.Service;


@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService{

}
