package com.gohb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.SysRole;
import com.gohb.domain.SysUser;
import com.gohb.vo.SysRoleVo;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    /**
     * 新增角色和权限
     *
     * @param sysRoleVo
     * @param userId
     */
    void saveRoleAndMenu(SysRoleVo sysRoleVo, Long userId);

    /**
     * 更新角色
     *
     * @param sysRoleVo
     * @return
     */
    boolean updateByRoleId(SysRoleVo sysRoleVo);
}
