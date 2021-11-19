package com.gohb.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.constant.AreaConstant;
import com.gohb.domain.Area;
import com.gohb.mapper.AreaMapper;
import com.gohb.service.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = "com.gohb.service.impl.AreaServiceImpl")
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    @Autowired
    private AreaMapper areaMapper;
    /**
     * 全查询区域信息
     *
     * @return
     */
    @Override
    @Cacheable(key = AreaConstant.ALL_AREA)
    public List<Area> list() {
        return areaMapper.selectList(null);
    }

    /**
     * 通过父id 查询子
     *
     * @param pId
     * @return
     */
    @Override
    public List<Area> listByPid(Long pId) {
        List<Area> areas = areaMapper.selectList(new LambdaQueryWrapper<Area>()
                .eq(Area::getParentId, pId)
        );
        if (ObjectUtils.isEmpty(areas)) {
            return Collections.emptyList();
        }
        return areas;
    }
    /**
     * 新增一个地区
     * 清理缓存
     *  @param area
     * @return
     */
    @Override
    @CacheEvict(key = AreaConstant.ALL_AREA)
    public boolean save(Area area) {
        log.info("新增一个区域{}", JSON.toJSONString(area));
        Long parentId = area.getParentId();
        if (parentId == null || parentId.equals(0L)) {
            area.setLevel(1);
        } else {
            //查询到父id 设置等级+1
            Area parent = areaMapper.selectById(parentId);
            if (ObjectUtils.isEmpty(parent)){
                throw new IllegalArgumentException("新增区域时父节点不存在");
            }
            area.setLevel(parent.getLevel() + 1);
        }
        return super.save(area);
    }

    /**
     * 删除一个区域
     *
     * @param id
     * @return
     */
    @Override
    @Caching(
            evict = {
                    @CacheEvict(key = "#id"),
                    @CacheEvict(key = AreaConstant.ALL_AREA)
            }
    )
    public boolean removeById(Serializable id) {
        log.info("删除一个区域id 为{}", id);
        //先判断他下面有没有子节点
        Integer count = areaMapper.selectCount(new LambdaQueryWrapper<Area>()
                .eq(Area::getParentId, id)
        );
        if (count != null && count > 0) {
            throw new IllegalArgumentException("删除的区域有子节点，不能删除,要删除的id 为" + id);
        }
        return super.removeById(id);
    }

    /**
     * 更新一个区域
     *
     * @param area
     * @return
     */
    @Override
    @Caching(
            evict = {
                    @CacheEvict(key = "area.areaId"),
                    @CacheEvict(key = AreaConstant.ALL_AREA)
            }
    )
    public boolean updateById(Area area) {
        return super.updateById(area);
    }
    /**
     * 查询放入缓存中
     *
     * @param id
     * @return
     */
    @Override
    @Cacheable(key = "#id")
    public Area getById(Serializable id) {
        return super.getById(id);
    }

}
