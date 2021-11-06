package com.gohb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.SysUser;
import com.gohb.mapper.SysUserMapper;
import com.gohb.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;


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
}