package com.gohb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.SysRole;
import com.gohb.domain.SysRoleMenu;
import com.gohb.domain.SysUser;
import com.gohb.mapper.SysRoleMapper;
import com.gohb.mapper.SysRoleMenuMapper;
import com.gohb.service.SysRoleService;
import com.gohb.vo.SysRoleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    /**
     * 新增角色和菜单
     *
     * @param sysRoleVo
     * @param userId
     */
    @Override
    @Transactional
    public void saveRoleAndMenu(SysRoleVo sysRoleVo, Long userId) {
        log.info("新增角色开始，操作人Id%s", userId);
        SysRole sysRole = new SysRole();
        //对象拷贝
        BeanUtil.copyProperties(sysRoleVo, sysRole, true);
        sysRole.setCreateUserId(userId);
        sysRole.setCreateTime(new Date());
        sysRoleMapper.insert(sysRole);
        log.info("新增角色后插入角色和菜单中间表");
        //得到菜单和权限列表
        List<String> menuIdList = sysRoleVo.getMenuIdList();
        for (String s : menuIdList) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(sysRole.getRoleId());
            sysRoleMenu.setMenuId(Long.valueOf(s));
            sysRoleMenuMapper.insert(sysRoleMenu);
        }
    }

    /**
    * 更新角色
    * 1.先更新角色
    * 2.更新权限（1.删除旧的值，2.插入新的值）
    *
    * @param sysRoleVo
    * @return
    */
    @Override
    @Transactional
    public boolean updateByRoleId(SysRoleVo sysRoleVo) {
        //1
        log.info("修改id 为%s 的角色", sysRoleVo.getRoleId());
        sysRoleVo.setCreateTime(new Date());
        SysRole sysRole = new SysRole();
        //对象拷贝
        BeanUtil.copyProperties(sysRoleVo, sysRole, true);
        int flag = sysRoleMapper.updateById(sysRole);
        if (flag > 0) {
            //2.1 删除旧的权限
            sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                    .eq(SysRoleMenu::getRoleId, sysRole.getRoleId())
            );
            //2.2 新增新的
            @NotEmpty List<String> menuIdList = sysRoleVo.getMenuIdList();
            for (String menuId : menuIdList) {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(sysRole.getRoleId());
                sysRoleMenu.setMenuId(Long.valueOf(menuId));
                sysRoleMenuMapper.insert(sysRoleMenu);
            }
        }
        return true;
    }


}