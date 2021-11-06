package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.SysUser;

public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询管理员列表
     *
     * @param page
     * @param sysUser
     * @return
     */
    IPage<SysUser> findSysUserByPage(Page<SysUser> page, SysUser sysUser);
}