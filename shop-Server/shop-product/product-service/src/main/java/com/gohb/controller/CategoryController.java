package com.gohb.controller;

import com.gohb.domain.Category;
import com.gohb.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类查询")
@RestController
@RequestMapping("prod/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping("table")
    @ApiOperation("分类全查询")
    @PreAuthorize("hasAuthority('prod:category:page')")
    public ResponseEntity<List<Category>> loadCategoryList() {
        List<Category> categoryList = categoryService.list();
        return ResponseEntity.ok(categoryList);
    }

    @DeleteMapping("{id}")
    @ApiOperation("删除商品分类")
    @PreAuthorize("hasAuthority('prod:category:delete')")
    public ResponseEntity<Void> deleteCateGory(@PathVariable Long id) {
        categoryService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info/{id}")
    @ApiOperation("获得一个分类对象")
    @PreAuthorize("hasAuthority('prod:category:info')")
    public ResponseEntity<Category> info(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return ResponseEntity.ok().body(category);
    }

    @PostMapping
    @ApiOperation("新增商品的分类")
    @PreAuthorize("hasAuthority('prod:category:save')")
    public ResponseEntity<Void> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @ApiOperation("更新分类")
    @PreAuthorize("hasAuthority('prod:category:update')")
    public ResponseEntity<Void> updateCateGory(@RequestBody @Validated Category category) {
        categoryService.updateById(category);
        return ResponseEntity.ok().build();
    }


    @GetMapping("listCategory")
    @ApiOperation("列出所有分类的父亲")
    @PreAuthorize("hasAuthority('prod:category:page')")
    public ResponseEntity<List<Category>> listCategory() {
        List<Category> categoryList = categoryService.listAllParent();
        return ResponseEntity.ok(categoryList);
    }

    //-------------------------前台代码
    @GetMapping("category/categoryInfo")
    @ApiOperation("前台分类信息")
    public ResponseEntity<List<Category>> getFrontCategoryInfo(@RequestParam("parentId") String parentId) {
        List<Category> categoryList = categoryService.listAllParent();
        return ResponseEntity.ok(categoryList);
    }


}
