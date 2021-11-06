package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.constant.MenuConstant;
import com.gohb.domain.SysMenu;
import com.gohb.mapper.SysMenuMapper;
import com.gohb.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import springfox.documentation.spring.web.json.Json;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<SysMenu> loadUserMenu(String userId) {
        log.info("加载树菜单");
        // 从缓存里面拿
        List<SysMenu> sysMenus = null;
        String menuStr = redisTemplate.opsForValue().get(MenuConstant.MENU_PREFIX + userId);
        if (StringUtils.isEmpty(menuStr)){
            sysMenus = sysMenuMapper.findMenuByUserId(Long.valueOf(menuStr));
            if (CollectionUtils.isEmpty(sysMenus)){
                // 说明该用户没有任何菜单
                return Collections.emptyList();
            }else {
                // 将用户菜单 转成json串 存入redis
                redisTemplate.opsForValue().set(MenuConstant.MENU_PREFIX + userId, JSON.toJSONString(sysMenus), Duration.ofDays(7));
            }
        }else {
            sysMenus = JSON.parseArray(menuStr, SysMenu.class);
        }
        return loadMenuTree(sysMenus, 0L);
    }

    /**
     * 组装树菜单
     * 使菜单具有层级结构
     * @param sysMenus 菜单
     * @param pid 父菜单id
     * @return
     */
    public List<SysMenu> loadMenuTree(List<SysMenu> sysMenus, Long pid){
        List<SysMenu> root = sysMenus.stream()
                .filter(sysMenu -> sysMenu.getParentId().equals(pid))
                .collect(Collectors.toList());
        root.forEach(r->{
            List<SysMenu> child = sysMenus.stream()
                    .filter(sysMenu -> sysMenu.getParentId().equals(r.getMenuId())).collect(Collectors.toList());
            r.setList(child);
        });
        return root;
    }

}