package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.Transport;

public interface TransportService extends IService<Transport>{

    /**
     * 分页查询运费模板
     *
     * @param page
     * @param condition
     * @return
     */
    IPage<Transport> findTransportPage(Page<Transport> page, Transport condition);

}
