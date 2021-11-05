package com.gohb.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 系统用户
 */
@ApiModel(value = "com-gohb-domain-SysUser")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user")
public class SysUser implements Serializable, UserDetails {

    @TableId(value = "user_id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @TableField(value = "mobile")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /**
     * 状态  0：禁用   1：正常
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "状态  0：禁用   1：正常")
    private Integer status;

    /**
     * 创建者ID
     */
    @TableField(value = "create_user_id")
    @ApiModelProperty(value = "创建者ID")
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 用户所在的商城Id
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value = "用户所在的商城Id")
    private Long shopId;


    /**
     * 权限集合
     */
    @TableField(exist = false)
    private List<String> auths;

    /**
     * 封装权限了
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        // 变成oauth认识的对象
        if (CollectionUtils.isEmpty(auths)) {
            return Collections.emptyList();
        }
        auths.forEach(auth -> {
            if (!StringUtils.isEmpty(auth)) {
                // 分割
                String[] split = auth.split(",");
                for (String s : split) {
                    // 循环封装
                    simpleGrantedAuthorities.add(new SimpleGrantedAuthority(s));
                }
            }
        });
        return simpleGrantedAuthorities;
    }


    // SecurityContextHolder 拿到当前用户信息

    /**
     * 因为一个系统里面需要用户id的
     * 用oauth生成的jwt格式很严格 不可以轻易改变
     *
     * @return
     */
    public String getUsername() {
        return String.valueOf(this.userId);
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 1;
    }
}