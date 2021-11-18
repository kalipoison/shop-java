package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.PickAddr;


public interface PickAddrService extends IService<PickAddr> {


    /**
     * 分页查询自提点
     *
     * @param page
     * @param pickAddr
     * @return
     */
    IPage<PickAddr> findPickAddrPage(Page<PickAddr> page, PickAddr pickAddr);
}
