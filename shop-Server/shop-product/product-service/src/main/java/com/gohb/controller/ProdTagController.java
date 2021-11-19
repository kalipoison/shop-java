package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.ProdTag;
import com.gohb.service.ProdTagService;
import com.gohb.vo.ProdTagVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "商品标签分组管理")
@RequestMapping("prod/prodTag")
public class ProdTagController {

    @Autowired
    private ProdTagService prodTagService;

    @GetMapping("/page")
    @ApiOperation("分页查询商品的标签")
    @PreAuthorize("hasAuthority('prod:prodTag:page')")
    public ResponseEntity<IPage<ProdTag>> findByPage(Page<ProdTag> page, ProdTag prodTag) {
        IPage<ProdTag> pageData = prodTagService.findProdByPage(page, prodTag);
        return ResponseEntity.ok(pageData);
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('prod:prodTag:info')")
    @ApiOperation("标签数据的回显")
    public ResponseEntity<ProdTag> findById(@PathVariable("id") Long id) {
        ProdTag prodTag = prodTagService.getById(id);
        return ResponseEntity.ok(prodTag);
    }

    @PostMapping
    @ApiOperation("新增一个商品的标签")
    @PreAuthorize("hasAuthority('prod:prodTag:save')")
    public ResponseEntity<Void> save(@RequestBody @Validated ProdTag prodTag) {
        prodTagService.save(prodTag);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('prod:prodTag:delete')")
    @ApiOperation("删除商品的标签")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        prodTagService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('prod:prodTag:delete')")
    @ApiOperation("删除多个商品的标签")
    public ResponseEntity<Void> delete(@RequestBody List<Long> ids) {
        prodTagService.removeByIds(ids);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @ApiOperation("新增一个商品的标签")
    @PreAuthorize("hasAuthority('prod:prodTag:save')")
    public ResponseEntity<Void> update(@RequestBody @Validated ProdTag prodTag) {
        prodTagService.updateById(prodTag);
        return ResponseEntity.ok().build();
    }

    @GetMapping({"/listTagList"})
    @ApiOperation("列出所有的商品标签的列表")
    @PreAuthorize("hasAuthority('prod:prodTag:info')")
    public ResponseEntity<List<ProdTag>> list() {
        List<ProdTag> list = prodTagService.list();
        return ResponseEntity.ok(list);
    }


    // -----------------------------前台代码


    @GetMapping("prodTagList")
    @ApiOperation("加载前台的标签分组")
    public ResponseEntity<List<ProdTagVo>> loadFrontProdTag() {
        List<ProdTagVo> prodTagVos = prodTagService.findProdTagVo();
        return ResponseEntity.ok(prodTagVos);
    }


}
