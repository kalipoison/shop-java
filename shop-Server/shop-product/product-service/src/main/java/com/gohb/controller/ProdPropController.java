package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.ProdProp;
import com.gohb.domain.ProdPropValue;
import com.gohb.service.ProdPropService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prod/spec")
@Api(tags = "商品的规格管理")
public class ProdPropController {

    @Autowired
    private ProdPropService prodPropService;

    @GetMapping("/page")
    @ApiOperation("分页查询商品的属性和属性值")
    @PreAuthorize("hasAuthority('prod:spec:page')")
    public ResponseEntity<IPage<ProdProp>> prodPropPage(Page<ProdProp> page, ProdProp prodProp) {
        IPage<ProdProp> prodPropIPage = prodPropService.findProdPropPage(page, prodProp);
        return ResponseEntity.ok(prodPropIPage);
    }

    @DeleteMapping({"/{id}"})
    @PreAuthorize("hasAuthority('prod:spec:delete')")
    @ApiOperation("删除属性")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        prodPropService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('prod:spec:save')")
    @ApiOperation("新增商品的属性和属性值")
    public ResponseEntity<Void> add(@RequestBody @Validated ProdProp prodProp) {
        prodPropService.save(prodProp);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('prod:spec:update')")
    @ApiOperation("修改一个属性的值")
    public ResponseEntity<Void> update(@RequestBody @Validated ProdProp prodProp) {
        prodPropService.updateById(prodProp);
        return ResponseEntity.ok().build();
    }

    @GetMapping({"/info/{id}"})
    @PreAuthorize("hasAuthority('prod:spec:info')")
    @ApiOperation("属性值的回显")
    public ResponseEntity<ProdProp> findById(@PathVariable("id") Long id) {
        ProdProp byId = prodPropService.getById(id);
        return ResponseEntity.ok(byId);
    }

    @GetMapping({"/list"})
    @PreAuthorize("hasAuthority('prod:spec:info')")
    @ApiOperation("查询商品的属性和属性值")
    public ResponseEntity<List<ProdProp>> list() {
        List<ProdProp> list = prodPropService.list();
        return ResponseEntity.ok(list);
    }

    @GetMapping({"/listSpecValue/{id}"})
    @PreAuthorize("hasAuthority('prod:spec:info')")
    @ApiOperation("根据查询商品的属性id查询属性值集合")
    public ResponseEntity<List<ProdPropValue>> listSpecValue(@PathVariable("id") Long id) {
        List<ProdPropValue> prodPropValue = prodPropService.findPropValuesByPropId(id);
        return ResponseEntity.ok(prodPropValue);
    }

}
