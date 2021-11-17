package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.Prod;
import com.gohb.service.ProdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prod/prod")
@Api(tags = "商品管理")
public class ProdController {

    @Autowired
    private ProdService prodService;

    @GetMapping("/page")
    @ApiOperation("分页查询商品")
    @PreAuthorize("hasAuthority('prod:prod:page')")
    public ResponseEntity<IPage<Prod>> page(Page<Prod> page, Prod prod) {
        IPage<Prod> prodIPage = prodService.findProdPage(page, prod);
        return ResponseEntity.ok(prodIPage);
    }

    @PostMapping
    @ApiOperation("新增商品")
    @PreAuthorize("hasAuthority('prod:prod:save')")
    public ResponseEntity<Void> addProd(@RequestBody @Validated Prod prod) {
        prodService.save(prod);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    @ApiOperation("删除一个商品")
    @PreAuthorize("hasAuthority('prod:prod:delete')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        prodService.removeById(id);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping
    @ApiOperation("删除多个商品")
    @PreAuthorize("hasAuthority('prod:prod:delete')")
    public ResponseEntity<Void> delete(@RequestBody List<Long> list) {
        prodService.removeByIds(list);
        return ResponseEntity.ok().build();
    }

    @GetMapping({"/info/{id}"})
    @ApiOperation("数据的回显")
    @PreAuthorize("hasAuthority('prod:prod:info')")
    public ResponseEntity<Prod> findById(@PathVariable("id") Long id) {
        Prod prod = prodService.getById(id);
        return ResponseEntity.ok(prod);
    }
    @PutMapping
    @ApiOperation("修改一个商品")
    @PreAuthorize("hasAuthority('prod:prod:update')")
    public ResponseEntity<Void> update(@RequestBody @Validated Prod prod) {
        prodService.updateById(prod);
        return ResponseEntity.ok().build();
    }


}
