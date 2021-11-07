package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.anno.Log;
import com.gohb.domain.SysRole;
import com.gohb.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "角色的接口")
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("list")
    @ApiOperation(value = "查询所有角色")
    @PreAuthorize("hasAuthority('sys:role:page')")
    public ResponseEntity<List<SysRole>> loadAllRole() {
        List<SysRole> roleList = sysRoleService.list();
        return ResponseEntity.ok(roleList);
    }

    @GetMapping("page")
    @ApiOperation(value = "分页查询管理员列表")
    @PreAuthorize("hasAuthority('sys:role:page')")
    @Log(operation = "分页查询角色列表")
    public ResponseEntity<IPage<SysRole>> getSysUserPage(Page<SysRole> page, SysRole sysRole) {
        IPage<SysRole> sysUserIPage = sysRoleService.findSysUserByPage(page, sysRole);
        return ResponseEntity.ok(sysUserIPage);
    }

}
