package com.gohb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.constant.ProdTagConstant;
import com.gohb.domain.ProdTag;
import com.gohb.domain.ProdTagReference;
import com.gohb.mapper.ProdTagMapper;
import com.gohb.mapper.ProdTagReferenceMapper;
import com.gohb.service.ProdTagService;
import com.gohb.vo.ProdTagVo;
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
@CacheConfig(cacheNames = "com.gohb.service.impl.ProdTagServiceImpl")
public class ProdTagServiceImpl extends ServiceImpl<ProdTagMapper, ProdTag> implements ProdTagService {

    @Autowired
    private ProdTagMapper prodTagMapper;
    @Autowired
    private ProdTagReferenceMapper prodTagReferenceMapper;


    /**
     * 分页查询商品标签（分组）
     *
     * @param page
     * @param prodTag
     * @return
     */
    @Override
    public IPage<ProdTag> findProdByPage(Page<ProdTag> page, ProdTag prodTag) {
        page.addOrder(OrderItem.desc("update_time"));
        IPage<ProdTag> prodTagIPage = prodTagMapper.selectPage(page, new LambdaQueryWrapper<ProdTag>()
                .like(StringUtils.hasText(prodTag.getTitle()), ProdTag::getTitle, prodTag.getTitle())
                .eq(prodTag.getStatus() != null, ProdTag::getStatus, prodTag.getStatus())
        );
        return prodTagIPage;
    }


    /**
     * 商品标签列表
     * @return
     */
    @Override
    @Cacheable(key = ProdTagConstant.PROD_TAG_PREFIX)
    public List<ProdTag> list() {
        List<ProdTag> prodTags = prodTagMapper.selectList(new LambdaQueryWrapper<ProdTag>()
                .eq(ProdTag::getStatus, 1)
        );
        return prodTags;
    }

    /**
     * 新增商品标签
     * @param prodTag
     * @return
     */
    @Override
    @CacheEvict(key = ProdTagConstant.PROD_TAG_PREFIX)
    public boolean save(ProdTag prodTag) {
        log.info("新增商品标签{}", JSON.toJSONString(prodTag));
        // 设置一些默认值
        prodTag.setCreateTime(new Date());
        prodTag.setUpdateTime(new Date());
        return super.save(prodTag);
    }

    /**
     * 删除商品的标签
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        log.info("删除商品的标签分组id 为{}", id);
        //查看有没有商品正在用此分组标签
        Integer count = prodTagReferenceMapper.selectCount(new LambdaQueryWrapper<ProdTagReference>()
                .eq(ProdTagReference::getTagId, id)
        );
        if (count > 0) {
            log.error("有商品正在使用此标签分组，不能删除");
            throw new IllegalArgumentException("有商品正在使用此标签分组，不能删除");
        }
        return super.removeById(id);
    }

    /**
     * 加载前台的标签分组
     *
     * @return
     */
    @Override
    public List<ProdTagVo> findProdTagVo() {
        List<ProdTag> prodTags = prodTagMapper.selectList(new LambdaQueryWrapper<ProdTag>()
                .eq(ProdTag::getStatus, 1)
                .orderByAsc(ProdTag::getSeq)
        );
        ArrayList<ProdTagVo> prodTagVos = new ArrayList<>(prodTags.size() * 2);
        prodTags.forEach(prodTag -> {
            ProdTagVo prodTagVo = new ProdTagVo();
            BeanUtil.copyProperties(prodTag, prodTagVo);
            prodTagVos.add(prodTagVo);
        });
        return prodTagVos;
    }

}
