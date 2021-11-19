package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.Transport;
import com.gohb.service.TransportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop/transport")
@Api(tags = "运费管理")
public class TransportController {

    @Autowired
    private TransportService transportService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('shop:transport:page') ")
    @ApiOperation("分页查询Transport")
    public ResponseEntity<IPage<Transport>> page(Page<Transport> page, Transport condition) {
        IPage<Transport> pageDate = transportService.findTransportPage(page, condition);
        return ResponseEntity.ok(pageDate);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('shop:transport:save') ")
    @ApiOperation("新增一个Transport")
    public ResponseEntity<Void> save(@RequestBody @Validated Transport entity) {
        transportService.save(entity);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('shop:transport:update') ")
    @ApiOperation("根据id 修改Transport")
    public ResponseEntity<Void> update(@RequestBody @Validated Transport entity) {
        transportService.updateById(entity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('shop:transport:delete') ")
    @ApiOperation("删除Transport")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        transportService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('shop:transport:info') ")
    @ApiOperation("查询Transport 详情")
    public ResponseEntity<Transport> info(@PathVariable("id") Long id) {
        Transport entity = transportService.getById(id);
        return ResponseEntity.ok(entity);
    }

}
