package com.gohb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.Category;

import java.util.List;


public interface CategoryService extends IService<Category> {

    /**
     * 查询商品的分类所有父节点所有的1级节点
     *
     * @return
     */
    List<Category> listAllParent();


}
