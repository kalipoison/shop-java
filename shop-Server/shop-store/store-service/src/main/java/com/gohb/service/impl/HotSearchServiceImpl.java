package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.HotSearch;
import com.gohb.mapper.HotSearchMapper;
import com.gohb.service.HotSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;


@Service
@Slf4j
public class HotSearchServiceImpl extends ServiceImpl<HotSearchMapper, HotSearch> implements HotSearchService {

    @Autowired
    private HotSearchMapper hotSearchMapper;

    /**
     * 分页查询热搜
     *
     * @param page
     * @param hotSearch
     * @return
     */
    @Override
    public IPage<HotSearch> findHotSearchPage(Page<HotSearch> page, HotSearch hotSearch) {
        return hotSearchMapper.selectPage(page, new LambdaQueryWrapper<HotSearch>()
                .eq(hotSearch.getStatus() != null, HotSearch::getStatus, hotSearch.getStatus())
                .like(StringUtils.hasText(hotSearch.getTitle()), HotSearch::getTitle, hotSearch.getTitle())
                .like(StringUtils.hasText(hotSearch.getContent()), HotSearch::getContent, hotSearch.getContent())
        );
    }

    @Override
    public boolean save(HotSearch hotSearch) {
        log.info("新增热搜{}", JSON.toJSONString(hotSearch));
        hotSearch.setShopId(1L);
        hotSearch.setRecDate(new Date());
        return super.save(hotSearch);
    }
}
