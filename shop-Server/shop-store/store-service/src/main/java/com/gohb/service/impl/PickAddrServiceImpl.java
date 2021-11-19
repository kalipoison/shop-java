package com.gohb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.PickAddr;
import com.gohb.mapper.PickAddrMapper;
import com.gohb.service.PickAddrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;


@Service
@Slf4j
@CacheConfig(cacheNames = "com.sxt.service.impl.PickAddrServiceImpl")
public class PickAddrServiceImpl extends ServiceImpl<PickAddrMapper, PickAddr> implements PickAddrService {

    @Autowired
    private PickAddrMapper pickAddrMapper;


    /**
     * 分页查询自提点
     *
     * @param page
     * @param pickAddr
     * @return
     */
    @Override
    public IPage<PickAddr> findPickAddrPage(Page<PickAddr> page, PickAddr pickAddr) {
        return pickAddrMapper.selectPage(page, new LambdaQueryWrapper<PickAddr>()
                .like(StringUtils.hasText(pickAddr.getAddrName()), PickAddr::getAddrName, pickAddr.getAddrName())
        );
    }

    @Cacheable(key = "#id")
    @Override
    public PickAddr getById(Serializable id) {
        return super.getById(id);
    }
    @CacheEvict(key = "#id")
    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
    @CacheEvict(key = "#entity.addrId")
    @Override
    public boolean updateById(PickAddr entity) {
        return super.updateById(entity);
    }
}
