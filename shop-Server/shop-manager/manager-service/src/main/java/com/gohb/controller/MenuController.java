package com.gohb.controller;

import com.gohb.anno.Log;
import com.gohb.domain.SysMenu;
import com.gohb.service.SysMenuService;
import com.gohb.vo.MenuAndAuths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Api(tags = "后台菜单接口")
@RestController
@RequestMapping("sys/menu")
public class MenuController {

    @Autowired
    private SysMenuService sysMenuService;


    /**
     * 加载菜单和权限接口
     * @return
     */
    @GetMapping("nav")
    @ApiOperation(value = "加载用户菜单和权限的接口")
    @Log(operation = "加载用户菜单和权限的接口")
    public ResponseEntity<MenuAndAuths> loadMenuAndAuths(){
        // 获取当前用户id
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<SysMenu> menus = sysMenuService.loadUserMenu(userId);
        // 获取权限
        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        // 转换n
        List<String> auths = new ArrayList<>();
        authorities.forEach(authoritie->auths.add(authoritie.getAuthority()));
        // 创建一个返回值
        MenuAndAuths menuAndAuths = new MenuAndAuths();
        menuAndAuths.setMenuList(menus);
        menuAndAuths.setAuthorities(auths);
        return ResponseEntity.ok(menuAndAuths);
    }

}
