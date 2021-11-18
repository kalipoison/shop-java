//package com.gohb.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.gohb.constant.AreaConstant;
//import com.gohb.domain.Area;
//import com.gohb.mapper.AreaMapper;
//import com.gohb.service.AreaService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//import org.springframework.util.ObjectUtils;
//
//import java.util.List;
//
//@Service
//@Slf4j
//@CacheConfig(cacheNames = "com.whsxt.service.impl.AreaServiceImpl")
//public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {
//
//    @Autowired
//    private AreaMapper areaMapper;
//
//
//    @Override
//    @Cacheable(key = AreaConstant.AREA_PREFIX)
//    public List<Area> list() {
//        return areaMapper.selectList(null);
//    }
//
//
//    /**
//     * 新增地址
//     * 新增地址有层级关系
//     *
//     * @param area
//     * @return
//     */
//    @Override
//    @CacheEvict(key = AreaConstant.AREA_PREFIX)
//    public boolean save(Area area) {
//        log.info("新增地址{}", JSON.toJSONString(area));
//        // 1 拿到parentId
//        Long parentId = area.getParentId();
//        if (parentId == null || parentId.equals(0L)) {
//            // 说明就是第一级节点
//            area.setLevel(1);
//        } else {
//            // 查询数据库得到父节点
//            Area parent = areaMapper.selectById(parentId);
//            if (ObjectUtils.isEmpty(parent)) {
//                throw new IllegalArgumentException("新增区域时父节点不存在");
//            }
//            area.setLevel(parent.getLevel() + 1);
//        }
//        return super.save(area);
//    }
//
//    /**
//     * 根据父id查询地址集合
//     *
//     * @param pid
//     * @return
//     */
//    @Override
//    public List<Area> findAreaByPid(Long pid) {
//        return areaMapper.selectList(new LambdaQueryWrapper<Area>()
//                .eq(Area::getParentId, pid)
//        );
//    }
//}
