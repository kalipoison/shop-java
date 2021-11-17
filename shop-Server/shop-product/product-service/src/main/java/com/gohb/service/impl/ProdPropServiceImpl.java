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

import java.io.Serializable;
import java.util.ArrayList;
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
     * @param page
     * @param prodProp
     * @return
     */
    @Override
    public IPage<ProdProp> findProdPropPage(Page<ProdProp> page, ProdProp prodProp) {
        // 分页查询所有属性
        Page<ProdProp> prodPropPage = prodPropMapper.selectPage(page, new LambdaQueryWrapper<ProdProp>()
                .like(StringUtils.hasText(prodProp.getPropName()), ProdProp::getPropName, prodProp.getPropName())
        );
        // 拿到当前页的数据
        List<ProdProp> prodPropList = prodPropPage.getRecords();
        // 没有一条属性
        if (CollectionUtils.isEmpty(prodPropList)){
            prodPropPage.setRecords(Collections.emptyList());
            return prodPropPage;
        }
        // 拿到所有属性的id， 属性和属性值是 一对多的关系
        // 拿到所有属性的ids
        List<Long> prodPropIds = prodPropList.stream()
                .map(ProdProp::getPropId)
                .collect(Collectors.toList());
        // 根据所有的ids 查询所有属性值的集合
        List<ProdPropValue> prodPropValues = prodPropValueMapper.selectList(
                new LambdaQueryWrapper<ProdPropValue>()
                        .in(ProdPropValue::getPropId, prodPropIds)
        );
        prodPropList.forEach(pp -> {
            List<ProdPropValue> propValues = prodPropValues.stream()
                    .filter(prodPropValue -> prodPropValue.getPropId().equals(pp.getPropId()))
                    .collect(Collectors.toList());
            pp.setProdPropValues(propValues);
        });
        return prodPropPage;
    }

    /**
     * 新增属性和属性值
     *
     * @param prodProp
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean save(ProdProp prodProp) {
        log.info("新增商品的属性和属性值{}", JSON.toJSONString(prodProp));
        int insert = prodPropMapper.insert(prodProp);
        if (insert > 0) {
            List<ProdPropValue> prodPropValues = prodProp.getProdPropValues();
            prodPropValues.forEach(prodPropValue -> prodPropValue.setPropId(prodProp.getPropId()));
            // 操作数据库
            prodPropValueService.saveBatch(prodPropValues);
        }
        return insert > 0;
    }



    /**
     * 根据查询商品的属性id查询属性值集合
     *
     * @param id
     * @return
     */
    @Override
    public List<ProdPropValue> findPropValuesByPropId(Long id) {
        List<ProdPropValue> prodPropValues = prodPropValueMapper.selectList(new
                LambdaQueryWrapper<ProdPropValue>()
                .eq(ProdPropValue::getPropId, id)
        );
        return prodPropValues;
    }

    /**
     * 根据商品id 删除商品的属性和属性值集合
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        int remove = prodPropMapper.deleteById(id);
        if (remove > 0){
            prodPropValueMapper.delete(new LambdaQueryWrapper<ProdPropValue>()
                .eq(ProdPropValue::getPropId, id));
        }
        return remove > 0;
    }

    @Override
    public boolean updateById(ProdProp prodProp) {
        int update = prodPropMapper.updateById(prodProp);
        if (update > 0) {
            List<ProdPropValue> prodPropValues = prodProp.getProdPropValues();
            // 更新时，可能插入新的数据prodPropValue，此时没有 prodId
            prodPropValues.forEach(prodPropValue ->
                    prodPropValue.setPropId(prodProp.getPropId()));
            prodPropValueService.saveOrUpdateBatch(prodPropValues);
        }
        return update > 0;
    }


    @Override
    public List<ProdProp> list() {
        // 查到属性 还要查到属性值
        List<ProdProp> prodProps = prodPropMapper.selectList(null);
        // 查询属性值
        List<Long> prodIds = prodProps.stream().map(ProdProp::getPropId)
                            .collect(Collectors.toList());
        // 查询属性值表
        List<ProdPropValue> prodPropValueList = prodPropValueMapper.selectList(new LambdaQueryWrapper<ProdPropValue>()
                .in(ProdPropValue::getPropId, prodIds));
        // 根据属性的ids 查询到属性值的集合 然后循环属性 判断属性值 组装数据
        prodProps.forEach(prodProp -> {
            List<ProdPropValue> prodPropValues = prodPropValueList.stream()
                    .filter(prodPropValue -> prodPropValue.getPropId().equals(prodProp.getPropId()))
                    .collect(Collectors.toList());
            prodProp.setProdPropValues(prodPropValues);
        });

//        List<Long> propIds = prodProps.stream()
//                .map(ProdProp::getPropId)
//                .collect(Collectors.toList());
//        // 查询属性值表
//        List<ProdPropValue> prodPropValueList = prodPropValueMapper.selectList(new LambdaQueryWrapper<ProdPropValue>()
//                .in(ProdPropValue::getPropId, propIds)
//        );
//        // 根据属性的ids 查询到属性值的集合 然后循环属性 判断属性值 组装数据
//        prodProps.forEach(prodProp -> {
//            List<ProdPropValue> prodPropValues = prodPropValueList.stream()
//                    .filter(prodPropValue -> prodPropValue.getPropId().equals(prodProp.getPropId()))
//                    .collect(Collectors.toList());
//            prodProp.setProdPropValues(prodPropValues);
//        });
        return prodProps;
    }
}
