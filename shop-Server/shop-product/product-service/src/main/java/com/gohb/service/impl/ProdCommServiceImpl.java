package com.gohb.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.Prod;
import com.gohb.domain.ProdComm;
import com.gohb.es.ProdEs;
import com.gohb.mapper.ProdCommMapper;
import com.gohb.mapper.ProdMapper;
import com.gohb.service.ProdCommService;
import com.gohb.vo.ProdCommVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProdCommServiceImpl extends ServiceImpl<ProdCommMapper, ProdComm> implements ProdCommService {

    @Autowired
    private ProdCommMapper prodCommMapper;
    @Autowired
    private ProdMapper prodMapper;

    /**
     * 分页查询商品的评论
     * 商品名称没查询
     *
     * @param page
     * @param prodComm
     * @return
     */
    @Override
    public IPage<ProdComm> findProdCommPage(Page<ProdComm> page, ProdComm prodComm) {
        // 排序
        page.addOrder(OrderItem.desc("rec_time"));
        String prodName = prodComm.getProdName();
        List<Long> prodIds = null;
        if (!StringUtils.isEmpty(prodName)){
            // 如果商品名称不为空, 先根据商品名称，查询商品表中 商品的ids
            List<Prod> prods = prodMapper.selectList(new LambdaQueryWrapper<Prod>()
                    .like(Prod::getProdName, prodName)
            );
            if (!CollectionUtils.isEmpty(prods)){
                // 拿到商品的ids
                prodIds = prods.stream().
                        map(Prod::getProdId).collect(Collectors.toList());
            }
        }
        // 分页查询评论
        Page<ProdComm> prodCommPage = prodCommMapper.selectPage(page, new LambdaQueryWrapper<ProdComm>()
                .eq(prodComm.getStatus() != null, ProdComm::getStatus, prodComm.getStatus())
                .in(!CollectionUtils.isEmpty(prodIds), ProdComm::getProdId, prodIds)
        );
        // 拿到评论了
        List<ProdComm> prodCommList = prodCommPage.getRecords();
        if (!CollectionUtils.isEmpty(prodCommList)){
            // 组织商品的名称
            // 根据商品评论集合中的ids，查询商品名称
            List<Long> newProdIds = prodCommList.stream().map(ProdComm::getProdId).collect(Collectors.toList());
            List<Prod> prods = prodMapper.selectBatchIds(newProdIds);
            // 组装数据
            prodCommList.forEach(pc -> {
                // 找到对应评论的商品
                Prod prod1 = prods.stream().filter(prod -> prod.getProdId().equals(pc.getProdId())).collect(Collectors.toList()).get(0);
                pc.setProdName(prod1.getProdName());
            });
        }
        return prodCommPage;

//        // 排序
//        page.addOrder(OrderItem.desc("rec_time"));
//        IPage<ProdComm> prodCommIPage = new Page<>();
//        //根据商品名称查询prodIds
//        String prodName = prodComm.getProdName();
//        List<Object> prodIds = null;
//        if (!StringUtils.isEmpty(prodName)) {
//            //如果有名称查询prodIds
//            prodIds = prodMapper.selectObjs(new LambdaQueryWrapper<Prod>()
//                    .select(Prod::getProdId)
//                    .like(Prod::getProdName, prodName)
//            );
//            if (CollectionUtils.isEmpty(prodIds)) {
//                prodCommIPage.setRecords(Collections.emptyList());
//                prodCommIPage.setTotal(0L);
//                return prodCommIPage;
//            }
//        }
//        //查询所有评论条件就是如果有prodIds 就带上没有就不带
//        prodCommIPage = prodCommMapper.selectPage(page, new LambdaQueryWrapper<ProdComm>()
//                .eq(prodComm.getStatus() != null, ProdComm::getStatus, prodComm.getStatus())
//                .in(!CollectionUtils.isEmpty(prodIds), ProdComm::getProdId, prodIds)
//        );
//        List<ProdComm> records = prodCommIPage.getRecords();
//        if (!CollectionUtils.isEmpty(records)) {
//            //拿到商品ids 然后查询商品表然后组装数据filter 过滤条件map 抽取collect 转换集合
//            //stream 流1 过程filter，map，sort，limit， 2 终止foreach collect
//            List<Long> pIds = records.stream().map(ProdComm::getProdId).collect(Collectors.toList());
//            List<Prod> prods = prodMapper.selectList(new LambdaQueryWrapper<Prod>()
//                    .in(Prod::getProdId, pIds)
//            );
//            //在代码里面做组装
//            records.forEach(r -> {
//                Prod prod1 = prods.stream()
//                        .filter(prod -> prod.getProdId().equals(r.getProdId()))
//                        .collect(Collectors.toList())
//                        .get(0);
//                r.setProdName(prod1.getProdName());
//            });
//        }
//        return prodCommIPage;
    }


//    /**
//     * 根据id 查询评论
//     *
//     * @param id
//     * @return
//     */
//    @Override
//    public ProdCommVo getProdCommById(Long id) {
//        ProdCommVo prodCommVo = new ProdCommVo();
//        ProdComm prodComm = prodCommMapper.selectOne(new LambdaQueryWrapper<ProdComm>()
//                .eq(ProdComm::getProdCommId, id)
//        );
//        if (ObjectUtils.isEmpty(prodComm)) {
////如果评论为空，则返回空
//            throw new IllegalArgumentException("评论id 为空");
//        }
//        Prod prod = prodMapper.selectOne(new LambdaQueryWrapper<Prod>()
//                .eq(Prod::getProdId, prodComm.getProdId())
//        );
//        if (!ObjectUtils.isEmpty(prod)) {
//            prodCommVo.setProdName(prod.getProdName());
//        }
//        BeanUtil.copyProperties(prodComm, prodCommVo, true);
//        return prodCommVo;
//    }
//
//    @Override
//    public ProdCommVo getProdCommAll(Long prodId) {
//        ProdCommVo prodCommVo = new ProdCommVo();
//        // 直接从es拿好评率
//        Optional<ProdEs> optionalProdEs = prodEsDao.findById(prodId);
//        ProdEs prodEs = optionalProdEs.get();
//        // 好评率
//        BigDecimal positiveRating = prodEs.getPositiveRating();
//        // 好评数
//        Long praiseNumber = prodEs.getPraiseNumber();
//        // 总评数 总的数量太多了 占内存 jvm可能直接爆炸
////        List<ProdComm> prodComms = prodCommMapper.selectList(new LambdaQueryWrapper<ProdComm>()
////                .eq(ProdComm::getProdId, prodId)
////        );
//        Integer totalCount = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>()
//                .eq(ProdComm::getProdId, prodId)
//        );
//        Integer secondCount = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>()
//                .eq(ProdComm::getProdId, prodId)
//                .eq(ProdComm::getEvaluate, 1)
//        );
//
//        Integer badCount = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>()
//                .eq(ProdComm::getProdId, prodId)
//                .eq(ProdComm::getEvaluate, 2)
//        );
//
//        Integer picCount = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>()
//                .eq(ProdComm::getProdId, prodId)
//                .isNotNull(ProdComm::getPics)
//        );
//
//        prodCommResult.setNumber(totalCount);
//        prodCommResult.setNegativeNumber(badCount);
//        prodCommResult.setSecondaryNumber(secondCount);
//        prodCommResult.setPicNumber(picCount);
//        prodCommResult.setPositiveRating(positiveRating);
//        prodCommResult.setPraiseNumber(praiseNumber);
//        return prodCommResult;
//    }


}
