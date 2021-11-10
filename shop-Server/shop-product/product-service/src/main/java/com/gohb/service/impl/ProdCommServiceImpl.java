package com.gohb.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.Prod;
import com.gohb.domain.ProdComm;
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

import java.util.Collections;
import java.util.List;
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
    public IPage<ProdComm> loadProdCommByPage(Page<ProdComm> page, ProdComm prodComm) {
        IPage<ProdComm> prodCommIPage = new Page<>();
        page.addOrder(OrderItem.desc("rec_time"));
//根据商品名称查询prodIds
        String prodName = prodComm.getProdName();
        List<Object> prodIds = null;
        if (!StringUtils.isEmpty(prodName)) {
//如果有名称查询prodIds
            prodIds = prodMapper.selectObjs(new LambdaQueryWrapper<Prod>()
                    .select(Prod::getProdId)
                    .like(Prod::getProdName, prodName)
            );
            if (CollectionUtils.isEmpty(prodIds)) {
                prodCommIPage.setRecords(Collections.emptyList());
                prodCommIPage.setTotal(0L);
                return prodCommIPage;
            }
        }
//查询所有评论条件就是如果有prodIds 就带上没有就不带
        prodCommIPage = prodCommMapper.selectPage(page, new LambdaQueryWrapper<ProdComm>()
                .eq(prodComm.getStatus() != null, ProdComm::getStatus, prodComm.getStatus())
                .in(!CollectionUtils.isEmpty(prodIds), ProdComm::getProdId, prodIds)
        );
        List<ProdComm> records = prodCommIPage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
//拿到商品ids 然后查询商品表然后组装数据filter 过滤条件map 抽取collect 转换集合
//stream 流1 过程filter，map，sort，limit， 2 终止foreach collect
            List<Long> pIds = records.stream().map(ProdComm::getProdId).collect(Collectors.toList());
            List<Prod> prods = prodMapper.selectList(new LambdaQueryWrapper<Prod>()
                    .in(Prod::getProdId, pIds)
            );
//在代码里面做组装
            records.forEach(r -> {
                Prod prod1 = prods.stream()
                        .filter(prod -> prod.getProdId().equals(r.getProdId()))
                        .collect(Collectors.toList())
                        .get(0);
                r.setProdName(prod1.getProdName());
            });
        }
        return prodCommIPage;
    }


    /**
     * 根据id 查询评论
     *
     * @param id
     * @return
     */
    @Override
    public ProdCommVo getProdCommById(Long id) {
        ProdCommVo prodCommVo = new ProdCommVo();
        ProdComm prodComm = prodCommMapper.selectOne(new LambdaQueryWrapper<ProdComm>()
                .eq(ProdComm::getProdCommId, id)
        );
        if (ObjectUtils.isEmpty(prodComm)) {
//如果评论为空，则返回空
            throw new IllegalArgumentException("评论id 为空");
        }
        Prod prod = prodMapper.selectOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getProdId, prodComm.getProdId())
        );
        if (!ObjectUtils.isEmpty(prod)) {
            prodCommVo.setProdName(prod.getProdName());
        }
        BeanUtil.copyProperties(prodComm, prodCommVo, true);
        return prodCommVo;
    }



}
