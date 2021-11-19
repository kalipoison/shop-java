package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.ProdTag;
import com.gohb.vo.ProdTagVo;

import java.util.List;

public interface ProdTagService extends IService<ProdTag> {


    /**
     * 分页查询商品标签（分组）
     *
     * @param page
     * @param prodTag
     * @return
     */
    IPage<ProdTag> findProdByPage(Page<ProdTag> page, ProdTag prodTag);

    /**
     * 加载前台的标签分组
     *
     * @return
     */
    List<ProdTagVo> findProdTagVo();
}
