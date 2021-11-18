package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.HotSearch;


public interface HotSearchService extends IService<HotSearch> {


    /**
     * 分页查询热搜
     *
     * @param page
     * @param hotSearch
     * @return
     */
    IPage<HotSearch> findHotSearchPage(Page<HotSearch> page, HotSearch hotSearch);
}
