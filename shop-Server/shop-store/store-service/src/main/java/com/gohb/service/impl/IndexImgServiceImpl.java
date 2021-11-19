package com.gohb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.constant.IndexImgConstant;
import com.gohb.domain.IndexImg;
import com.gohb.domain.Prod;
import com.gohb.feign.IndexImgProdFeign;
import com.gohb.mapper.IndexImgMapper;
import com.gohb.service.IndexImgService;
import com.gohb.vo.IndexImgVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = "com.gohb.service.impl.IndexImgServiceImpl")
public class IndexImgServiceImpl extends ServiceImpl<IndexImgMapper, IndexImg> implements IndexImgService {

    @Autowired
    private IndexImgMapper indexImgMapper;

    @Autowired
    private IndexImgProdFeign indexImgProdFeign;

    /**
     * 分页查询轮播图
     *
     * @param page
     * @param indexImg
     * @return
     */
    @Override
    public IPage<IndexImg> findIndexImgPage(Page<IndexImg> page, IndexImg indexImg) {
        // 排序
        page.addOrder(OrderItem.asc("seq"));
        return indexImgMapper.selectPage(page, new LambdaQueryWrapper<IndexImg>()
                .eq(!ObjectUtils.isEmpty(indexImg.getStatus()), IndexImg::getStatus, indexImg.getStatus())
        );
    }

    /**
     * 因为轮播图要和商品关联
     * 新增
     *
     * @param indexImg
     * @return
     */
    @Override
    @CacheEvict(key = IndexImgConstant.INDEX_ALL_IMGS)
    public boolean save(IndexImg indexImg) {
        indexImg.setUploadTime(new Date());
        return super.save(indexImg);
    }

    @CacheEvict(key = IndexImgConstant.INDEX_ALL_IMGS)
    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @CacheEvict(key = IndexImgConstant.INDEX_ALL_IMGS)
    @Override
    public boolean updateById(IndexImg entity) {
        return super.updateById(entity);
    }

    /**
     * 轮播图的加载
     *
     * @return
     */
    @Override
    @Cacheable(key = IndexImgConstant.INDEX_ALL_IMGS)
    public List<IndexImgVo> loadIndexImgs() {
        List<IndexImg> indexImgs = indexImgMapper.selectList(new LambdaQueryWrapper<IndexImg>()
                .eq(IndexImg::getStatus, 1)
                .orderByAsc(IndexImg::getSeq) //前端根据这个排序
        );
        //转换对象
        List<IndexImgVo> indexImgVos = new ArrayList<>();
        if (indexImgs != null && indexImgs.size() > 0) {
            indexImgs.forEach(indexImg -> {
                IndexImgVo indexImgVo = new IndexImgVo();
                indexImgVo.setImgUrl(indexImg.getImgUrl());
                indexImgVo.setRelation(indexImg.getRelation());
                indexImgVos.add(indexImgVo);
            });
        }
        return indexImgVos;
    }


    /**
     * 轮播图和商品的对应信息
     *
     * @param id
     * @return
     */
    @Override
    @Cacheable(key = "#{id}")
    public IndexImg getInfo(Long id) {
        //根据轮播图id 查询商品
        IndexImg indexImg = indexImgMapper.selectById(id);
        if (ObjectUtils.isEmpty(indexImg)) {
            throw new IllegalArgumentException("查询的轮播图id 不存在");
        }
        if (indexImg.getRelation() == null) {
            return indexImg;
        }
        //远程调用商品的服务查询商品
        Prod prod = indexImgProdFeign.findProdById(indexImg.getRelation());
        if (ObjectUtils.isEmpty(prod)){
            indexImg.setProdName(prod.getProdName());
            indexImg.setPic(prod.getPic());
        }
        return indexImg;
    }

    /**
     * 加载前台轮播图接口
     * 可以做缓存的
     *
     * @return
     */
    @Override
    @Cacheable(key = IndexImgConstant.INDEX_ALL_IMGS)
    public List<IndexImgVo> findFrontIndexImg() {
        // 1. 查询数据库
        List<IndexImg> indexImgs = indexImgMapper.selectList(new LambdaQueryWrapper<IndexImg>()
                .eq(IndexImg::getStatus, 1)
                .orderByAsc(IndexImg::getSeq)
        );
        if (CollectionUtils.isEmpty(indexImgs)) {
            return Collections.emptyList();
        }
        // 如果不等于空
        // 转换对象
        ArrayList<IndexImgVo> indexImgVos = new ArrayList<>(indexImgs.size() * 2);
        indexImgs.forEach(indexImg -> {
            IndexImgVo indexImgVo = new IndexImgVo();
            BeanUtil.copyProperties(indexImg, indexImgVo, true);
            indexImgVos.add(indexImgVo);
        });
        return indexImgVos;
    }

}
