package com.gohb.controller;

import com.gohb.anno.Log;
import com.gohb.domain.SysMenu;
import com.gohb.service.SysMenuService;
import com.gohb.vo.MenuAndAuths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @Autowired
    private StringRedisTemplate redisTemplate;

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


    @GetMapping("/table")
    @ApiOperation("加载所有的菜单列表")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public ResponseEntity<List<SysMenu>> loadAllMenuList() {
        List<SysMenu> sysMenus = null;
        if (redisTemplate.hasKey(ManagerConstant.MENU)) {
            String menuStr = redisTemplate.opsForValue().get(ManagerConstant.MENU);
            sysMenus = JSON.parseArray(menuStr, SysMenu.class);
        } else {
            sysMenus = sysMenuService.list();
            String menuStr = JSON.toJSONString(sysMenus);
            redisTemplate.opsForValue().set(ManagerConstant.MENU, menuStr, Duration.ofDays(1));
        }
        return ResponseEntity.ok(sysMenus);
    }

    @GetMapping("/list")
    @ApiOperation("查询所有父菜单")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public ResponseEntity<List<SysMenu>> loadParentMenuList() {
        //查询所有父菜单，type 不等于2 的not equals
        List<SysMenu> sysMenus = sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                .ne(SysMenu::getType, 2)
        );
        return ResponseEntity.ok(sysMenus);
    }

    @ApiOperation("新增一个菜单")
    @PostMapping
    public ResponseEntity<Void> add(@RequestBody @Validated SysMenu sysMenu) {
        sysMenuService.save(sysMenu);
        return ResponseEntity.ok().build();
    }
    @ApiOperation("删除一个菜单")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        sysMenuService.removeById(id);
        return ResponseEntity.ok().build();
    }
    @ApiOperation("菜单数据的回显")
    @PreAuthorize("hasAuthority('sys:menu:info')")
    @GetMapping("/info/{id}")
    public ResponseEntity<SysMenu> findById(@PathVariable("id") Long id) {
        SysMenu byId = sysMenuService.getById(id);
        return ResponseEntity.ok(byId);
    }

    @PutMapping
    @ApiOperation("修改菜单数据")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public ResponseEntity<Void> update(@RequestBody @Validated SysMenu sysMenu) {
        sysMenuService.updateById(sysMenu);
        return ResponseEntity.ok().build();
    }

}
