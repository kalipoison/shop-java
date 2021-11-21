package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.Prod;
import com.gohb.domain.Sku;
import com.gohb.service.ProdService;
import com.gohb.service.SkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prod/prod")
@Api(tags = "商品管理")
public class ProdController {

    @Autowired
    private ProdService prodService;

    @Autowired
    private SkuService skuService;

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
    @ApiOperation("查询某个商品的详细信息")
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

    //-------------------------远程调用代码

    /**
     * 提供调用根据id查询商品信息
     *
     * @param prodId
     * @return
     */
    @GetMapping("/findProdById")
    @ApiOperation("新增商品")
    Prod findProdById(@RequestParam("prodId") Long prodId) {
        return prodService.getById(prodId);
    }


    /**
     * 远程调用根据skuIds查询sku的集合
     *
     * @param skuIds
     * @return
     */
    @PostMapping("/getSkuByIds")
    @ApiOperation("根据skuIds查询sku的集合")
    List<Sku> getSkuByIds(@RequestBody List<Long> skuIds) {
            return skuService.listByIds(skuIds);
    }

    //-----------------------------前台代码

    @GetMapping("prod/prodInfo")
    @ApiOperation("前台根据id查询商品的信息（包括了sku）")
    public ResponseEntity<Prod> frontFindProdById(Long prodId) {
        Prod prod = prodService.findProdAndSkuById(prodId);
        return ResponseEntity.ok(prod);
    }



//
//
//    /**
//     * 修改库存的方法
//     *
//     * @param stockMap
//     */
//    @PostMapping("changeStock")
//    @ApiOperation("修改库存的方法")
//    void changeStock(@RequestBody Map<String, Map<Long, Integer>> stockMap) {
//        prodService.changeStock(stockMap);
//    }



}
