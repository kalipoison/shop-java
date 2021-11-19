package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.Notice;
import com.gohb.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop/notice")
@Api(tags = "公告管理")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping({"/page"})
    @PreAuthorize("hasAuthority('shop:notice:page')")
    @ApiOperation("分页查询公告")
    public ResponseEntity<IPage<Notice>> findByPage(Page<Notice> page, Notice notice) {
        IPage<Notice> pageData = this.noticeService.findNoticePage(page, notice);
        return ResponseEntity.ok(pageData);
    }

    @GetMapping({"/info/{id}"})
    @PreAuthorize("hasAuthority('shop:notice:info')")
    @ApiOperation("公告的回显")
    public ResponseEntity<Notice> findById(@PathVariable("id") Long id) {
        Notice notice = noticeService.getById(id);
        return ResponseEntity.ok(notice);
    }

    @DeleteMapping({"/{id}"})
    @PreAuthorize("hasAuthority('shop:notice:delete')")
    @ApiOperation("删除公告")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        this.noticeService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('shop:notice:update')")
    @ApiOperation("修改公告")
    public ResponseEntity<Void> update(@RequestBody @Validated Notice notice) {
        this.noticeService.updateById(notice);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('shop:notice:save')")
    @ApiOperation("新增公告")
    public ResponseEntity<Void> save(@RequestBody @Validated Notice notice) {
        this.noticeService.save(notice);
        return ResponseEntity.ok().build();
    }

}
