package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.ProdProp;
import com.gohb.domain.ProdPropValue;
import com.gohb.mapper.ProdPropMapper;
import com.gohb.mapper.ProdPropValueMapper;
import com.gohb.service.ProdPropService;
import com.gohb.service.ProdPropValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProdPropServiceImpl extends ServiceImpl<ProdPropMapper, ProdProp> implements ProdPropService {

    @Autowired
    private ProdPropMapper prodPropMapper;


    @Autowired
    private ProdPropValueMapper prodPropValueMapper;

    @Autowired
    private ProdPropValueService prodPropValueService;

    /**
     * 分页查询商品的属性和属性值
     *
     * @param page
     * @param prodProp
     * @return
     */
    @Override
    public IPage<ProdProp> findProdPropPage(Page<ProdProp> page, ProdProp prodProp) {
        // 分页查询所有的属性
        Page<ProdProp> prodPropPage = prodPropMapper.selectPage(page, new LambdaQueryWrapper<ProdProp>()
                .like(StringUtils.hasText(prodProp.getPropName()), ProdProp::getPropName, prodProp.getPropName())
        );
        // 拿到当前页的数据
        List<ProdProp> prodPropList = prodPropPage.getRecords();
        // 属性都没有 直接返回了
        if (CollectionUtils.isEmpty(prodPropList)) {
            prodPropPage.setRecords(Collections.emptyList());
            return prodPropPage;
        }
        // 拿到所有的属性id  属性  80 81  和属性值 384 385 386 387 388 389 是一对多的关系
        // 拿到所有的属性的ids
        List<Long> prodPropIds = prodPropList.stream()
                .map(ProdProp::getPropId)
                .collect(Collectors.toList());
        // 根据ids 查询所有的属性值的集合
        List<ProdPropValue> prodPropValues = prodPropValueMapper.selectList(new LambdaQueryWrapper<ProdPropValue>()
                .in(ProdPropValue::getPropId, prodPropIds)
        );
        // 在java代码里面循环组装数据
        prodPropList.forEach(pp -> {
            // 判断条件
            List<ProdPropValue> propValues = prodPropValues.stream()
                    .filter(prodPropValue -> prodPropValue.getPropId().equals(pp.getPropId()))
                    .collect(Collectors.toList());
            pp.setProdPropValues(propValues);
        });
        return prodPropPage;
    }

    /**
     * 根据查询商品的属性id查询属性值集合
     *
     * @param id
     * @return
     */
    @Override
    public List<ProdPropValue> findPropValuesByPropId(Long id) {
        return prodPropValueMapper.selectList(new LambdaQueryWrapper<ProdPropValue>()
                .eq(ProdPropValue::getPropId, id)
        );
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean save(ProdProp prodProp) {
        // 操作两个表
        log.info("新增商品的属性和属性值{}", JSON.toJSONString(prodProp));
        // 新增属性表
        prodProp.setShopId(1L);
        prodProp.setRule(2);
        // 新增
        int insert = prodPropMapper.insert(prodProp);
        if (insert > 0) {
            // 新增属性值
            List<ProdPropValue> prodPropValues = prodProp.getProdPropValues();
            // 循环这个属性值的集合 统一设置id
            prodPropValues.forEach(prodPropValue -> prodPropValue.setPropId(prodProp.getPropId()));
            // 操作数据库
            prodPropValueService.saveBatch(prodPropValues);
        }
        return insert > 0;
    }

    @Override
    public List<ProdProp> list() {
        // 查到属性 还要查到属性值
        List<ProdProp> prodProps = prodPropMapper.selectList(null);
        // 查询属性值
        List<Long> propIds = prodProps.stream()
                .map(ProdProp::getPropId)
                .collect(Collectors.toList());
        // 查询属性值表
        List<ProdPropValue> prodPropValueList = prodPropValueMapper.selectList(new LambdaQueryWrapper<ProdPropValue>()
                .in(ProdPropValue::getPropId, propIds)
        );
        // 根据属性的ids 查询到属性值的集合 然后循环属性 判断属性值 组装数据
        prodProps.forEach(prodProp -> {
            List<ProdPropValue> prodPropValues = prodPropValueList.stream()
                    .filter(prodPropValue -> prodPropValue.getPropId().equals(prodProp.getPropId()))
                    .collect(Collectors.toList());
            prodProp.setProdPropValues(prodPropValues);
        });
        return prodProps;
    }
}
