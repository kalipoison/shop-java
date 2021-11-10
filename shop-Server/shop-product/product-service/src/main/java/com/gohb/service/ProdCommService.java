package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.ProdComm;
import com.gohb.vo.ProdCommVo;


public interface ProdCommService extends IService<ProdComm> {

    /**
     * 分页查询商品的评论
     *
     * @param page
     * @param prodComm
     * @return
     */
    IPage<ProdComm> loadProdCommByPage(Page<ProdComm> page, ProdComm prodComm);

    /**
     * 根据id 查询评论
     *
     * @param id
     * @return
     */
    ProdCommVo getProdCommById(Long id);
}

}
