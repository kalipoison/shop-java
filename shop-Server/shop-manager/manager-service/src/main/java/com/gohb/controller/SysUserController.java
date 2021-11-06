package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.SysUser;
import com.gohb.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "后台用户的接口")
@RestController
@RequestMapping("sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("info")
    @ApiOperation(value = "查询当前登录用户的信息")
    public ResponseEntity<SysUser> getUserInfo() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        SysUser sysUser = sysUserService.getById(userId);
        return ResponseEntity.ok(sysUser);
    }

    @GetMapping("page")
    @ApiOperation(value = "分页查询管理员列表")
    @PreAuthorize("hasAuthority('sys:user:page')")
    public ResponseEntity<IPage<SysUser>> getSysUserPage(Page<SysUser> page, SysUser sysUser){
        IPage<SysUser> sysUserIPage = sysUserService.findSysUserByPage(page, sysUser);
        return ResponseEntity.ok(sysUserIPage);
    }

}