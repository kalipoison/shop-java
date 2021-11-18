package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.IndexImg;
import com.gohb.vo.IndexImgVo;

import java.util.List;


public interface IndexImgService extends IService<IndexImg> {


    /**
     * 分页查询轮播图
     *
     * @param page
     * @param indexImg
     * @return
     */
    IPage<IndexImg> findIndexImgPage(Page<IndexImg> page, IndexImg indexImg);

    /**
     * 加载前台轮播图接口
     *
     * @return
     */
    List<IndexImgVo> loadIndexImgs();


    /**
     * 根据id查询
     * 轮播图和商品的对应信息
     *
     * @param id
     * @return
     */
    IndexImg getInfo(Long id);

}
