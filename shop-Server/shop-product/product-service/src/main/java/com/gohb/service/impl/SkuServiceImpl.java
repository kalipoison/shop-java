package com.gohb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.Sku;
import com.gohb.mapper.SkuMapper;
import com.gohb.service.SkuService;
import org.springframework.stereotype.Service;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService{

}
