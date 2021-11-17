package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.Prod;

import java.util.Date;
import java.util.Map;

public interface ProdService extends IService<Prod> {

    /**
     * 分页查询商品
     *
     * @param page
     * @param prod
     * @return
     */
    IPage<Prod> findProdPage(Page<Prod> page, Prod prod);

    /**
     * 查询时间段内的商品总条数
     *
     * @param t1
     * @param t2
     * @return
     */
    Integer getTotalCount(Date t1, Date t2);

    /**
     * 分页查询商品数据导入es 的
     *
     * @param page
     * @param t1
     * @param t2
     * @return
     */
    Page<Prod> findProdByPageToEs(Page<Prod> page, Date t1, Date t2);

}
