package com.gohb.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gohb.constant.AuthConstant;
import com.gohb.domain.MyUserDetail;
import com.gohb.domain.SysUser;
import com.gohb.domain.User;
import com.gohb.mapper.MyUserDetailMapper;
import com.gohb.mapper.SysUserMapper;
import com.gohb.mapper.UserMapper;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MyUserDetailMapper myUserDetailMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.appsecret}")
    private String appsecret;

    @Value("${wx.url}")
    private String WX_AUTH_TOKEN_URL;

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
                String url = String.format(WX_AUTH_TOKEN_URL, appid, appsecret, username);
                String wxStr = restTemplate.getForObject(url, String.class);
                // 转换json
                JSONObject wxJson = JSON.parseObject(wxStr);
                String openid = wxJson.getString("openid");
                if (!StringUtils.isEmpty(openid)){
                    // 查询数据库mysql
                    // 如果有，就登录，否则，就注册
                    MyUserDetail myUserDetail = myUserDetailMapper.selectById(openid);
//
//                    User user = userMapper.selectById(openid);
                    if (ObjectUtils.isEmpty(myUserDetail)){
                        // 注册
                        myUserDetail = addUser(openid);
                    }
                    return myUserDetail;
                }

                // 查询前台用户表
                // 拿到code
                // 组装url 发请求到微信
                // 拿到openId作为用户唯一标识
                return null;
            default:
                return null;
        }
    }

    /**
     * 注册用户的方法 因为 微信不需要注册 所以当用户第一登录我们就需要给他注册
     *
     * @param openid
     * @return
     */
    private MyUserDetail addUser(String openid) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String ip = request.getRemoteAddr();
        User user = new User();
        user.setUserId(openid);
        user.setStatus(1);
        user.setUserRegtime(new Date());
        user.setUserLasttime(new Date());
        user.setModifyTime(new Date());
        user.setUserLastip(ip);
        userMapper.insert(user);
        // 组装返回值 返回
        MyUserDetail myUserDetail = new MyUserDetail();
        myUserDetail.setUserId(openid);
        myUserDetail.setStatus(1);
        return myUserDetail;
    }


}
