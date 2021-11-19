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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
@CacheConfig(cacheNames = "com.gohb.service.impl.HotSearchServiceImpl")
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

    @Cacheable(key = "#id")
    @Override
    public HotSearch getById(Serializable id) {
        return super.getById(id);
    }

    @CacheEvict(key = "#entity.hotSearchId")
    @Override
    public boolean updateById(HotSearch entity) {
        return super.updateById(entity);
    }

    @CacheEvict(key = "#id")
    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean save(HotSearch hotSearch) {
        log.info("新增热搜{}", JSON.toJSONString(hotSearch));
        hotSearch.setRecDate(new Date());
        return super.save(hotSearch);
    }


    @Override
    public List<HotSearch> findHotSearchByShopId(Integer number, Integer shopId, Integer sort) {
        List<HotSearch> hotSearches = hotSearchMapper.selectList(new LambdaQueryWrapper<HotSearch>()
                .eq(HotSearch::getShopId, shopId)
        );
        List<HotSearch> hotList = new ArrayList<>(number);
        for (HotSearch hotSearch : hotSearches){
            hotList.add(hotSearch);
        }
        return hotList;
    }
}
