package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.SysRole;
import com.gohb.domain.SysUser;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     *
     * @param page
     * @param sysRole
     * @return
     */
    IPage<SysRole> findSysUserByPage(Page<SysRole> page, SysRole sysRole);

}
