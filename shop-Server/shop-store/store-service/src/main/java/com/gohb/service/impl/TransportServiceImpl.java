package com.gohb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.Transport;
import com.gohb.mapper.TransportMapper;
import com.gohb.service.TransportService;
import org.springframework.stereotype.Service;

@Service
public class TransportServiceImpl extends ServiceImpl<TransportMapper, Transport> implements TransportService{

}
