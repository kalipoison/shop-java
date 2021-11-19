package com.gohb.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.es.ProdEs;
import com.gohb.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api("商品搜索模块管理")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/prod/prodListByTagId")
    @ApiOperation("根据标签分组查询商品")
    public ResponseEntity<Page<ProdEs>> searchProdByTagId(Long tagId, @RequestParam(required = false, defaultValue = "0") Integer current, @RequestParam(required = false, defaultValue = "6") Integer size) {
        Page<ProdEs> prodByTagId = searchService.findProdByTagId(tagId, current, size);
        return ResponseEntity.ok(prodByTagId);
    }


    @GetMapping("search/searchProdPage")
    @ApiOperation("根据关键字查询商品")
    public ResponseEntity<Page<ProdEs>> searchProdByTagId(String prodName,
                                                          @RequestParam(required = false, defaultValue = "0") Integer current,
                                                          @RequestParam(required = false, defaultValue = "10") Integer size,
                                                          @RequestParam(required = false, defaultValue = "0") Integer sort) {
        Page<ProdEs> prodByKeywords = searchService.findProdByKeywords(prodName, current, size, sort);
        return ResponseEntity.ok(prodByKeywords);
    }


    @GetMapping("/prod/pageProd")
    @ApiOperation("根据分类id分页查询商品")
    public ResponseEntity<Page<ProdEs>> searchProdByCategoryId(Long cateGoryId,
                                                               @RequestParam(required = false, defaultValue = "0") Integer current,
                                                               @RequestParam(required = false, defaultValue = "10") Integer size) {
        Page<ProdEs> prodByCategoryId = searchService.findProdByCategoryId(cateGoryId, current, size);
        return ResponseEntity.ok(prodByCategoryId);
    }


    /**
     * 提供远程调用根据ids查询商品信息
     *
     * @param prodIds
     * @return
     */
    @PostMapping("/findProdEsByIds")
    List<ProdEs> findProdEsByIds(@RequestBody List<Long> prodIds) {
        return searchService.findProdByIds(prodIds);
    }



}
