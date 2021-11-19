package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.HotSearch;

import java.util.List;


public interface HotSearchService extends IService<HotSearch> {


    /**
     * 分页查询热搜
     *
     * @param page
     * @param hotSearch
     * @return
     */
    IPage<HotSearch> findHotSearchPage(Page<HotSearch> page, HotSearch hotSearch);


    /**
     * 前台热搜词
     * @param number
     * @param shopId
     * @param sort
     * @return
     */
    List<HotSearch> findHotSearchByShopId(Integer number, Integer shopId, Integer sort);
}
