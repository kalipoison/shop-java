package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.UserCollection;
import com.gohb.es.ProdEs;


public interface UserCollectionService extends IService<UserCollection> {


    /**
     * 查询用户收藏的数量
     *
     * @param openId
     * @return
     */
    Integer getCollectionCount(String openId);


    /**
     * 分页查询用户收藏的商品
     *
     * @param page
     * @param openId
     * @return
     */
    IPage<ProdEs> findUserCollectProd(Page<UserCollection> page, String openId);


    /**
     * 查询用户是否收藏该商品
     *
     * @param prodId
     * @param openId
     * @return
     */
    Boolean findUserIsCollect(Long prodId, String openId);

    /**
     * 用户添加或者取消收藏该商品
     *
     * @param prodId
     * @param openId
     */
    void addOrCancelCollect(Long prodId, String openId);
}
