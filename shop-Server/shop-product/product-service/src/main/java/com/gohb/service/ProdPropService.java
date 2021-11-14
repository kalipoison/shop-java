package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.ProdProp;
import com.gohb.domain.ProdPropValue;

import java.util.List;


public interface ProdPropService extends IService<ProdProp> {


    /**
     * 分页查询商品的属性和属性值
     *
     * @param page
     * @param prodProp
     * @return
     */
    IPage<ProdProp> findProdPropPage(Page<ProdProp> page, ProdProp prodProp);

    /**
     * 属性值的回显
     *
     * @param id
     * @return
     */
    List<ProdPropValue> findPropValuesByPropId(Long id);
}
