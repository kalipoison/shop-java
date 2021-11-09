package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.constant.SysUserConstant;
import com.gohb.domain.SysRole;
import com.gohb.domain.SysUser;
import com.gohb.domain.SysUserRole;
import com.gohb.mapper.SysUserMapper;
import com.gohb.service.SysUserRoleService;
import com.gohb.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public SysUser getById(Serializable id) {
        SysUser sysUser = null;
        String sysUserStr = redisTemplate.opsForValue().get(SysUserConstant.SYS_USER_PREFIX + id);
        if (StringUtils.isEmpty(sysUserStr)) {
            //缓存无
            sysUser = super.getById(id);
            //放缓存
            redisTemplate.opsForValue().set(SysUserConstant.SYS_USER_PREFIX + id,
                    JSON.toJSONString(sysUser));
        } else {
            //缓存有
            sysUser = JSON.parseObject(sysUserStr, SysUser.class);
        }
        return sysUser;
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