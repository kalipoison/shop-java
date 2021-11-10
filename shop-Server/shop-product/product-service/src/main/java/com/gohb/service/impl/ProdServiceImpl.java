package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.Prod;
import com.gohb.domain.ProdComm;
import com.gohb.domain.ProdTagReference;
import com.gohb.domain.Sku;
import com.gohb.mapper.*;
import com.gohb.service.ProdService;
import com.gohb.service.ProdTagReferenceService;
import com.gohb.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@CacheConfig(cacheNames = "com.gohb.service.impl.ProdServiceImpl")
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod> implements ProdService {

    @Autowired
    private ProdMapper prodMapper;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ProdTagReferenceMapper prodTagReferenceMapper;

    @Autowired
    private ProdCommMapper prodCommMapper;


    @Autowired
    private ProdTagReferenceService prodTagReferenceService;

    /**
     * 分页查询商品
     *
     * @param page
     * @param prod
     * @return
     */
    @Override
    public IPage<Prod> findProdPage(Page<Prod> page, Prod prod) {
        page.addOrder(OrderItem.desc("create_time"));
        return prodMapper.selectPage(page, new LambdaQueryWrapper<Prod>()
                .eq(prod.getStatus() != null, Prod::getStatus, prod.getStatus())
                .like(StringUtils.hasText(prod.getProdName()), Prod::getProdName, prod.getProdName())
        );
    }


    /**
     * 商品新增 需要写三张表
     * Prod
     * Sku
     * tag_reference
     *
     * @param prod
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean save(Prod prod) {
        log.info("新增商品{}", JSON.toJSONString(prod));
        prod.setCreateTime(new Date());
        prod.setUpdateTime(new Date());
        if (prod.getStatus().equals(1)) {
            prod.setPutawayTime(new Date());
        }
        prod.setSoldNum(0);
        prod.setShopId(1L);
        // 配送方式
        Prod.DeliverModeVo deliverModeVo = prod.getDeliveryModeVo();
        String deliverStr = JSON.toJSONString(deliverModeVo);
        prod.setDeliveryMode(deliverStr);
        // 插入数据库
        int insert = prodMapper.insert(prod);
        if (insert > 0) {
            // 操作Sku
            Sku sku = prod.getSkuList().get(0);
            sku.setActualStocks(prod.getTotalStocks());
            handlerSku(prod.getSkuList(), prod.getProdId());
            // 操作tag_reference
            handlerTagReference(prod.getTagList(), prod.getProdId());
        }
        return insert > 0;
    }

    /**
     * 处理商品和标签的中间表
     *
     * @param tagList
     * @param prodId
     */
    private void handlerTagReference(List<Long> tagList, Long prodId) {
        if (CollectionUtils.isEmpty(tagList)) {
            return;
        }
        ArrayList<ProdTagReference> prodTagReferences = new ArrayList<>(tagList.size() * 2);
        tagList.forEach(tag -> {
            ProdTagReference prodTagReference = new ProdTagReference();
            prodTagReference.setProdId(prodId);
            prodTagReference.setTagId(tag);
            prodTagReference.setCreateTime(new Date());
            prodTagReference.setStatus(Boolean.TRUE);
            prodTagReference.setShopId(1L);
            prodTagReferences.add(prodTagReference);
        });

        // 插入数据库
        prodTagReferenceService.saveBatch(prodTagReferences);
    }

    /**
     * 处理商品和sku的关系
     *
     * @param skuList
     * @param prodId
     */
    private void handlerSku(List<Sku> skuList, Long prodId) {
        skuList.forEach(sku -> {
            sku.setRecTime(new Date());
            sku.setUpdateTime(new Date());
            sku.setIsDelete(0);
            sku.setProdId(prodId);
            sku.setVersion(0);
        });
        // 插入sku的数据库
        skuService.saveBatch(skuList);
    }
    // ------------------------------- 导入的代码


    /**
     * 根据区间查询商品的总条数
     *
     * @param t1
     * @param t2
     * @return
     */
    @Override
    public Integer getTotalCount(Date t1, Date t2) {
        Integer count = prodMapper.selectCount(new LambdaQueryWrapper<Prod>()
                .between(t1 != null && t2 != null, Prod::getUpdateTime, t1, t2)
        );
        return count;
    }


    /**
     * 分页查询需要导入的商品
     * 1. 肯定查询数据库
     * 2. 组装数据
     * 2.1 处理标签
     * 2.2 处理好评和好评率
     *
     * @param page
     * @param t1
     * @param t2
     * @return
     */
    @Override
    public Page<Prod> findProdByPageToEs(Page<Prod> page, Date t1, Date t2) {
        Page<Prod> prodPage = prodMapper.selectPage(page, new LambdaQueryWrapper<Prod>()
                .between(t1 != null && t2 != null, Prod::getUpdateTime, t1, t2)
        );

        List<Prod> prodList = prodPage.getRecords();

        if (CollectionUtils.isEmpty(prodList)) {
            return prodPage;
        }
        //  1. 处理标签
        // 拿到商品ids
        List<Long> prodIds = prodList.stream()
                .map(Prod::getProdId)
                .collect(Collectors.toList());
        // 查询标签表
        List<ProdTagReference> tagReferenceList = prodTagReferenceMapper.selectList(new LambdaQueryWrapper<ProdTagReference>()
                .in(ProdTagReference::getProdId, prodIds)
        );
        // 循环组装数据
        prodList.forEach(prod -> {
            List<ProdTagReference> referenceList = tagReferenceList.stream()
                    .filter(tag -> tag.getProdId().equals(prod.getProdId()))
                    .collect(Collectors.toList());
            // 拿到tagId的list
            List<Long> tagList = referenceList.stream()
                    .map(ProdTagReference::getTagId)
                    .collect(Collectors.toList());
            prod.setTagList(tagList);
        });
        // 2. 处理好评和好评率
        // 查询评论表
        List<ProdComm> prodCommList = prodCommMapper.selectList(new LambdaQueryWrapper<ProdComm>()
                .in(ProdComm::getProdId, prodIds)
        );
        // 得到总评的数量的
        prodList.forEach(prod -> {
            Boolean flag = true;
            // 拿到这个商品的总评数量
            List<ProdComm> totalComm = prodCommList.stream()
                    .filter(prodComm -> prodComm.getProdId().equals(prod.getProdId()))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(totalComm)) {
                // 有总评数量
                List<ProdComm> goodsComm = totalComm.stream()
                        .filter(prodComm -> prodComm.getEvaluate().equals(0))
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(goodsComm)) {
                    flag = false;
                    // 计算好评数
                    int goodsSize = goodsComm.size();
                    // 计算总评数
                    int totalSize = totalComm.size();
                    // 转换
                    BigDecimal goodsSizeBigDecimal = new BigDecimal(goodsSize);
                    BigDecimal totalSizeBigDecimal = new BigDecimal(totalSize);
                    // 计算好评率
                    BigDecimal goodsLv = goodsSizeBigDecimal.divide(totalSizeBigDecimal, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    // 设置好评率
                    prod.setPositiveRating(goodsLv);
                    // 设置好评数
                    prod.setPraiseNumber((long) goodsSize);
                }
            }
            if (flag) {
                // 设置好评率
                prod.setPositiveRating(BigDecimal.ZERO);
                // 设置好评数
                prod.setPraiseNumber(0L);
            }
        });
        return prodPage;
    }

    /**
     * 前台根据id查询商品的信息（包括了sku）
     *
     * @param prodId
     * @return
     */
    @Override
    @Cacheable(key = "#prodId")
    public Prod findProdAndSkuById(Long prodId) {
        // 查询数据库
        Prod prod = prodMapper.selectById(prodId);
        if (ObjectUtils.isEmpty(prod)) {
            return null;
        }
        // 如果有商品 查询sku的集合
        List<Sku> list = skuService.list(new LambdaQueryWrapper<Sku>()
                .eq(Sku::getProdId, prod.getProdId())
        );
        prod.setSkuList(list);
        return prod;
    }

    /**
     * 修改库存的方法
     * 操作两个表
     *
     * @param stockMap
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void changeStock(Map<String, Map<Long, Integer>> stockMap) {
        // 拿到prod
        Map<Long, Integer> prodStock = stockMap.get("prod");
        // 先把匹配的的prod查出来
        Set<Long> prodIds = prodStock.keySet();
        List<Prod> prodList = prodMapper.selectBatchIds(prodIds);
        // 在代码中循环 修改库存 最后更新
        prodList.forEach(prod -> {
            Integer stock = prodStock.get(prod.getProdId());
            int finalStock = prod.getTotalStocks() + stock;
            if (finalStock < 0) {
                // 库存不足
                throw new IllegalArgumentException("库存不足");
            }
            prod.setTotalStocks(finalStock);
            prod.setUpdateTime(new Date());
        });
        // 统一修改
        this.updateBatchById(prodList);

        Map<Long, Integer> skuStock = stockMap.get("sku");
        Set<Long> skuIds = skuStock.keySet();
        List<Sku> skuList = skuService.listByIds(skuIds);
        skuList.forEach(sku -> {
            Integer stock = skuStock.get(sku.getSkuId());
            int finalStock = sku.getActualStocks() + stock;
            if (finalStock < 0) {
                // 库存不足
                throw new IllegalArgumentException("库存不足");
            }
            sku.setActualStocks(finalStock);
            sku.setStocks(finalStock);
            sku.setUpdateTime(new Date());
        });
        skuService.updateBatchById(skuList);
    }
}