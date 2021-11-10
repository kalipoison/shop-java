package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.ProdTag;
import com.gohb.vo.ProdTagVo;

import java.util.List;

public interface ProdTagService extends IService<ProdTag> {


    /**
     * 商品标签的分页查询
     *
     * @param page
     * @param prodTag
     * @return
     */
    IPage<ProdTag> findProdTagPage(Page<ProdTag> page, ProdTag prodTag);

    /**
     * 加载前台的标签分组
     *
     * @return
     */
    List<ProdTagVo> findProdTagVo();

}
