package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.PickAddr;
import com.gohb.service.PickAddrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("shop/pickAddr")
@Api(tags = "自提点管理")
public class PickAddrController {

    @Autowired
    private PickAddrService pickAddrService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('shop:pickAddr:page')")
    @ApiOperation("分页查询PickAddr")
    public ResponseEntity<IPage<PickAddr>> page(Page<PickAddr> page, PickAddr condition) {
        IPage<PickAddr> pageDate = pickAddrService.findPickAddrPage(page, condition);
        return ResponseEntity.ok(pageDate);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('shop:pickAddr:save')")
    @ApiOperation("新增一个PickAddr")
    public ResponseEntity<Void> save(@RequestBody @Validated PickAddr entity) {
        pickAddrService.save(entity);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('shop:pickAddr:update')")
    @ApiOperation("根据id 修改PickAddr")
    public ResponseEntity<Void> update(@RequestBody @Validated PickAddr entity) {
        pickAddrService.updateById(entity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('shop:pickAddr:delete')")
    @ApiOperation("删除PickAddr")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        pickAddrService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('shop:pickAddr:info')")
    @ApiOperation("查询PickAddr 详情")
    public ResponseEntity<PickAddr> info(@PathVariable("id") Long id) {
        PickAddr entity = pickAddrService.getById(id);
        return ResponseEntity.ok(entity);
    }


}
