package com.gohb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.anno.Log;
import com.gohb.domain.SysRole;
import com.gohb.service.SysRoleService;
import com.gohb.vo.SysRoleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        Page<SysRole> sysUserIPage = sysRoleService.page(page, new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(sysRole.getRoleName()), SysRole::getRoleName, sysRole.getRoleName())
        );
        return ResponseEntity.ok(sysUserIPage);
    }

    @DeleteMapping
    @ApiOperation("删除角色")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public ResponseEntity<Void> delete(@RequestBody List<Long> ids) {
        sysRoleService.removeByIds(ids);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @ApiModelProperty("新增一个角色")
    @PreAuthorize("hasAuthority('sys:role:save')")
    public ResponseEntity<Void> add(@RequestBody @Validated SysRoleVo sysRoleVo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        sysRoleService.saveRoleAndMenu(sysRoleVo, Long.valueOf(userId));
        return ResponseEntity.ok().build();
    }

    @ApiModelProperty("查询一个角色")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:role:info') ")
    public ResponseEntity<SysRole> findById(@PathVariable("id") Long id) {
        SysRole byId = sysRoleService.getById(id);
        return ResponseEntity.ok(byId);
    }

    @PutMapping
    @ApiModelProperty("修改角色数据")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public ResponseEntity<Void> update(@RequestBody @Validated SysRoleVo sysRoleVo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        //先设置更新操作人是谁
        sysRoleVo.setCreateUserId(Long.valueOf(userId));
        //更新角色
        sysRoleService.updateByRoleId(sysRoleVo);
        return ResponseEntity.ok().build();
    }

}
