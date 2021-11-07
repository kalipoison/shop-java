package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.constant.RoleConstant;
import com.gohb.domain.SysRole;
import com.gohb.domain.SysUser;
import com.gohb.mapper.SysRoleMapper;
import com.gohb.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    /**
     * 查询角色的列表
     *
     * @return
     */
    @Override
    public List<SysRole> list() {
        log.info("全查询角色列表");
        String roleStr = redisTemplate.opsForValue().get(RoleConstant.ROLE_PREFIX);
        List<SysRole> sysRoles = null;
        if (StringUtils.isEmpty(roleStr)){
            sysRoles = sysRoleMapper.selectList(null);
            if (CollectionUtils.isEmpty(sysRoles)){
                return Collections.emptyList();
            }
            redisTemplate.opsForValue().set(RoleConstant.ROLE_PREFIX, JSON.toJSONString(sysRoles), Duration.ofDays(7));
        }else {
            sysRoles = JSON.parseArray(roleStr, SysRole.class);
        }
        return sysRoles;
    }


    @Override
    public IPage<SysRole> findSysUserByPage(Page<SysRole> page, SysRole sysRole) {
        page.addOrder(OrderItem.desc("create_time"));
        return sysRoleMapper.selectPage(page, new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(sysRole.getRoleName()), SysRole::getRoleName, sysRole.getRoleName()));
    }
}