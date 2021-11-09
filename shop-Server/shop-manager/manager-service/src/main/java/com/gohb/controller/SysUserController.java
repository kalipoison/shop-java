package com.gohb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.anno.Log;
import com.gohb.domain.SysUser;
import com.gohb.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Log(operation = "查询当前登录用户的信息")
    public ResponseEntity<SysUser> getUserInfo() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        SysUser sysUser = sysUserService.getById(userId);
        return ResponseEntity.ok(sysUser);
    }

    @GetMapping("page")
    @ApiOperation(value = "分页查询管理员列表")
    @PreAuthorize("hasAuthority('sys:user:page')")
    @Log(operation = "分页查询管理员列表")
    public ResponseEntity<IPage<SysUser>> getSysUserPage(Page<SysUser> page, SysUser sysUser){
        IPage<SysUser> sysUserIPage = sysUserService.page(page, new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(sysUser.getUsername()), SysUser::getUsername, sysUser.getUsername())
        );
        return ResponseEntity.ok(sysUserIPage);
    }

    /**
     * 删除一个用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除一个用户")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @Log(operation = "删除一个用户")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        sysUserService.removeById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除多个用户
     */
    @DeleteMapping
    @ApiOperation("删除多个值")
    @Log(operation = "删除多个用户")
    public ResponseEntity<Void> delete(@RequestBody List<Long> ids) {
        sysUserService.removeByIds(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 回显
     */
    @GetMapping("/info/{id}")
    @ApiOperation("用户的回显")
    @PreAuthorize("hasAuthority('sys:user:info')")
    public ResponseEntity<SysUser> findById(@PathVariable("id") Long id) {
        SysUser sysUser = sysUserService.getById(id);
        return ResponseEntity.ok(sysUser);
    }


    @PostMapping
    @ApiOperation("新增用户")
    @PreAuthorize("hasAuthority('sys:user:save')")
    @Log(operation = "新增一个用户")
    public ResponseEntity<Void> saveSysUser(@RequestBody SysUser sysUser) {
        // 拿到当前用户的id
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        sysUser.setCreateUserId(Long.valueOf(userId));
        sysUserService.save(sysUser);
        return ResponseEntity.ok().build();
    }

    /**
     * 修改用户
     *
     * @param sysUser
     * @return
     */
    @PutMapping
    @ApiOperation("修改用户")
    @PreAuthorize("hasAuthority('sys:user:update')")
    @Log(operation = "修改一个用户")
    public ResponseEntity<Void> update(@RequestBody @Validated SysUser sysUser) {
        sysUser.setPassword(new BCryptPasswordEncoder().encode(sysUser.getPassword()));
        sysUserService.updateById(sysUser);
        return ResponseEntity.ok().build();
    }


}