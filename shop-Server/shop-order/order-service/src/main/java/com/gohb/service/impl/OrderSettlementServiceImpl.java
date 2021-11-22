package com.gohb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.OrderSettlement;
import com.gohb.mapper.OrderSettlementMapper;
import com.gohb.service.OrderSettlementService;
import org.springframework.stereotype.Service;

@Service
public class OrderSettlementServiceImpl extends ServiceImpl<OrderSettlementMapper, OrderSettlement> implements OrderSettlementService{

}
