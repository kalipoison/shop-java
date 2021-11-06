package com.gohb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    /**
     * 加载菜单
     *
     * @param userId
     * @return
     */
    List<SysMenu> loadUserMenu(String userId);
}
