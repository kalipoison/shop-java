package com.gohb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gohb.domain.UserAddr;
import com.gohb.service.UserAddrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api("用户收货地址管理")
public class UserAddrController {


    @Autowired
    private UserAddrService userAddrService;


    /**
     * 全查询用户的收货地址
     * @return
     */
    @GetMapping("p/address/list")
    @ApiOperation("全查询用户的收货地址")
    public ResponseEntity<List<UserAddr>> loadAllUserAddr() {
        // 当前用户
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<UserAddr> userAddrs = userAddrService.findUserAddr(openId);
        return ResponseEntity.ok(userAddrs);
    }


    /**
     * 新增用户收货地址
     * @param userAddr
     * @return
     */
    @PostMapping("p/address/addAddr")
    @ApiOperation("新增收货地址")
    public ResponseEntity<Void> addUserAddr(@RequestBody UserAddr userAddr) {
        // 当前用户
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        userAddr.setUserId(openId);
        userAddrService.save(userAddr);
        return ResponseEntity.ok().build();
    }



    /**
     * 查询用户收货地址详细信息
     * @param id
     * @return
     */
    @GetMapping("/p/address/addrInfo/{id}")
    @ApiOperation("查询用户地址的详情")
    public ResponseEntity<UserAddr> info(@PathVariable("id") Long id) {
        UserAddr entity = this.userAddrService.getById(id);
        return ResponseEntity.ok(entity);
    }

    /**
     * 删除收货地址信息
     * @param id
     * @return
     */
    @DeleteMapping({"/p/address/deleteAddr/{id}"})
    @ApiOperation("删除用户地址信息")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        this.userAddrService.removeById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 设置或修改默认收货地址信息
     * @param id
     * @return
     */
    @PutMapping("/p/address/defaultAddr/{id}")
    @ApiOperation("设置或修改默认收货地址")
    public ResponseEntity<Void> changeDefaultAddr(@PathVariable("id") Long id) {
        // 当前用户
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        userAddrService.changeUserDefaultAddr(openId, id);
        return ResponseEntity.ok().build();
    }

    /**
     * 修改用户收货地址
     * @param userAddr
     * @return
     */
    @PutMapping("/p/address/updateAddr")
    @ApiOperation("根据id 修改UserAddr")
    public ResponseEntity<Void> update(@RequestBody @Validated UserAddr userAddr) {
        String openId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        userAddr.setUserId(openId);
        userAddrService.updateById(userAddr);
        return ResponseEntity.ok().build();
    }

    // ---------------------------远程调用

    /**
     * 查询用户的默认收货地址
     *
     * @param openId
     * @return
     */
    @GetMapping("p/address/getDefaultAddr")
    @ApiOperation("查询用户的默认收货地址")
    UserAddr getDefaultAddr(@RequestParam("openId") String openId) {
        return userAddrService.getOne(new LambdaQueryWrapper<UserAddr>()
                .eq(UserAddr::getUserId, openId)
                .eq(UserAddr::getCommonAddr, 1)
        );
    }


}
