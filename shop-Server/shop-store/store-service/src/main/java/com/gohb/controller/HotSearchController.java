package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.HotSearch;
import com.gohb.service.HotSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("admin/hotSearch")
@Api(tags = "热搜管理")
public class HotSearchController {

    @Autowired
    private HotSearchService hotSearchService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('admin:hotSearch:page')")
    @ApiOperation("热搜的分页查询")
    public ResponseEntity<IPage<HotSearch>> findByPage(Page<HotSearch> page, HotSearch
            hotSearch) {
        IPage<HotSearch> pageData = hotSearchService.findHotSearchPage(page, hotSearch);
        return ResponseEntity.ok(pageData);
    }
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('admin:hotSearch:info') ")
    @ApiOperation("热搜的回显")
    public ResponseEntity<HotSearch> findById(@PathVariable("id") Long id) {
        HotSearch byId = hotSearchService.getById(id);
        return ResponseEntity.ok(byId);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:hotSearch:delete')")
    @ApiOperation("删除热搜")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        hotSearchService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:hotSearch:save')")
    @ApiOperation("新增一个热搜")
    public ResponseEntity<Void> add(@RequestBody @Validated HotSearch hotSearch) {
        hotSearch.setRecDate(new Date());
        hotSearchService.save(hotSearch);
        return ResponseEntity.ok().build();
    }
    @PutMapping
    @PreAuthorize("hasAuthority('admin:hotSearch:update')")
    @ApiOperation("新增一个热搜")
    public ResponseEntity<Void> update(@RequestBody @Validated HotSearch hotSearch) {
        hotSearchService.updateById(hotSearch);
        return ResponseEntity.ok().build();
    }


}
