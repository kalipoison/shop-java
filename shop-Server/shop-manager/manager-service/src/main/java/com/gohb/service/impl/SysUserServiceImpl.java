package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.SysRole;
import com.gohb.domain.SysUser;
import com.gohb.domain.SysUserRole;
import com.gohb.mapper.SysUserMapper;
import com.gohb.service.SysUserRoleService;
import com.gohb.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 分页查询管理员列表
     *
     * @param page
     * @param sysUser
     * @return
     */
    @Override
    public IPage<SysUser> findSysUserByPage(Page<SysUser> page, SysUser sysUser) {
        page.addOrder(OrderItem.desc("create_time"));
        return sysUserMapper.selectPage(page, new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(sysUser.getUsername()), SysUser::getUsername, sysUser.getUsername())
                .eq(sysUser.getStatus() != null, SysUser::getStatus, sysUser.getStatus()));
    }

    /**
     * 新增用户
     * 把用户的增删改查写完
     *
     * @param sysUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean save(SysUser sysUser) {
        log.info("新增用户，操作员id:{}，新增内容为:{}", sysUser.getCreateUserId(), JSON.toJSONString(sysUser));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(sysUser.getPassword());
        sysUser.setPassword(encode);
        sysUser.setCreateTime(new Date());
        sysUser.setShopId(1L);
        // 新增
        int insert = sysUserMapper.insert(sysUser);
        if (insert > 0){
            Long userId = sysUser.getUserId();
            // 操作中间表
            List<Long> roleIds = sysUser.getRoleIdList();
            List<SysUserRole> sysUserRoles = new ArrayList<>();
            roleIds.forEach(roleId->{
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId(roleId);
                sysUserRoles.add(sysUserRole);
            });
            sysUserRoleService.saveBatch(sysUserRoles);
        }
        return insert > 0;
    }
}