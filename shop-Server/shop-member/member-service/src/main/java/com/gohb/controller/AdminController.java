package com.gohb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.User;
import com.gohb.service.UserService;
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

@RestController
@RequestMapping("/admin/user")
@Api("后台会员管理模块接口")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("page")
    @ApiOperation("分页查询会员")
//    @PreAuthorize("hasAuthority('admin:user:page')")
    public ResponseEntity<IPage<User>> hotSearchPage(Page<User> page, User user) {
        Page<User> memberIPage = userService.page(page, new LambdaQueryWrapper<User>()
                .like(!StringUtils.isEmpty(user.getUserId()), User::getUserId, user.getUserId())
        );
        return ResponseEntity.ok(memberIPage);
    }

    /**
     * 删除一个用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除一个用户")
//    @PreAuthorize("hasAuthority('admin:user:delete')")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        userService.removeById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除多个用户
     */
    @DeleteMapping
    @ApiOperation("删除多个值")
    public ResponseEntity<Void> delete(@RequestBody List<String> ids) {
        userService.removeByIds(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 回显
     */
    @GetMapping("/info/{id}")
    @ApiOperation("用户的回显")
//    @PreAuthorize("hasAuthority('admin:user:info')")
    public ResponseEntity<User> findById(@PathVariable("id") String id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(user);
    }


    /**
     * 修改用户
     *
     * @return
     */
    @PutMapping
    @ApiOperation("修改用户")
//    @PreAuthorize("hasAuthority('admin:user:update')")
    public ResponseEntity<Void> update(@RequestBody @Validated User user) {
        userService.updateById(user);
        return ResponseEntity.ok().build();
    }

}
