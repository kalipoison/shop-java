package com.gohb.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gohb.constant.AuthConstant;
import com.gohb.domain.SysUser;
import com.gohb.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 登录方法
     * 前台用户和后台用户用一套登录
     * 在header放一个标识
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ServletRequestAttributes requestAttributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        // 获取头信息
        String loginType = request.getHeader(AuthConstant.LOGIN_TYPE);
        if (StringUtils.isEmpty(loginType)){
            return null;
        }
        // 选择
        switch (loginType){
            // 后台用户 查sys_user表
            case AuthConstant.SYS_USER:
                SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username));
                if (!ObjectUtils.isEmpty(sysUser)){
                    List<String> auths = sysUserMapper.findUserAuthsById(sysUser.getUserId());
                    if (!CollectionUtils.isEmpty(auths)){
                        sysUser.setAuths(auths);
                    }
                }
                return sysUser;
                // 前台用户
            case AuthConstant.MEMBER:
                return null;
            default:
                return null;
        }
    }



}
