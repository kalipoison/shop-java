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
import com.gohb.mapper.ProdTagMapper;
import com.gohb.service.ProdTagService;
import com.gohb.vo.ProdTagVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = "com.gohb.service.impl.ProdTagServiceImpl")
public class ProdTagServiceImpl extends ServiceImpl<ProdTagMapper, ProdTag> implements ProdTagService {

    @Autowired
    private ProdTagMapper prodTagMapper;


    /**
     * 商品标签的分页查询
     *
     * @param page
     * @param prodTag
     * @return
     */
    @Override
    public IPage<ProdTag> findProdTagPage(Page<ProdTag> page, ProdTag prodTag) {
        // 排序
        page.addOrder(OrderItem.desc("update_time"));
        return prodTagMapper.selectPage(page, new LambdaQueryWrapper<ProdTag>()
                .like(StringUtils.hasText(prodTag.getTitle()), ProdTag::getTitle, prodTag.getTitle())
                .eq(prodTag.getStatus() != null, ProdTag::getStatus, prodTag.getStatus())
        );
    }


    @Override
    @CacheEvict(key = ProdTagConstant.PROD_TAG_PREFIX)
    public boolean save(ProdTag prodTag) {
        log.info("新增商品标签{}", JSON.toJSONString(prodTag));
        // 新增就设置一些默认的值  例如时间
        prodTag.setCreateTime(new Date());
        prodTag.setUpdateTime(new Date());
        prodTag.setShopId(1L);
        return super.save(prodTag);
    }

    @Override
    @Cacheable(key = ProdTagConstant.PROD_TAG_PREFIX)
    public List<ProdTag> list() {
        List<ProdTag> prodTags = prodTagMapper.selectList(new LambdaQueryWrapper<ProdTag>()
                .eq(ProdTag::getStatus, 1)
        );
        return prodTags;
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
