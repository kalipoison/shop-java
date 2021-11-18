package com.gohb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.Area;

import java.util.List;


public interface AreaService extends IService<Area> {


    /**
     * 根据父id查询地址集合
     *
     * @param pid
     * @return
     */
    List<Area> findAreaByPid(Long pid);
}
