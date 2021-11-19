package com.gohb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.Area;

import java.util.List;


public interface AreaService extends IService<Area> {


    /**
     * 通过父id 查询子
     *
     * @param pId
     * @return
     */
    List<Area> listByPid(Long pId);
}
