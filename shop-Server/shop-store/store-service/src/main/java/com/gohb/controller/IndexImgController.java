package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.IndexImg;
import com.gohb.service.IndexImgService;
import com.gohb.vo.IndexImgVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/admin/indexImg")
@Api(tags = "轮播图管理接口")
public class IndexImgController {

    @Autowired
    private IndexImgService indexImgService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('admin:indexImg:page')")
    @ApiOperation("分页查询轮播图")
    public ResponseEntity<IPage<IndexImg>> page(Page<IndexImg> page, IndexImg indexImg) {
        IPage<IndexImg> indexImgIPage = indexImgService.findIndexImgPage(page, indexImg);
        return ResponseEntity.ok(indexImgIPage);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:indexImg:save')")
    @ApiOperation("新增轮播图")
    public ResponseEntity<Void> save(@RequestBody @Validated IndexImg indexImg) {
        indexImgService.save(indexImg);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('admin:indexImg:update')")
    @ApiOperation("根据id 修改轮播图")
    public ResponseEntity<Void> update(@RequestBody @Validated IndexImg entity) {
        indexImgService.updateById(entity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:indexImg:delete')")
    @ApiOperation("删除IndexImg")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        indexImgService.removeById(id);
        return ResponseEntity.ok().build();
    }

//    /**
//     * 轮播图的加载
//     */
//    @GetMapping("/indexImgs")
//    public ResponseEntity<List<IndexImgVo>> loadIndexImgs() {
//        List<IndexImgVo> indexImgList = indexImgService.loadIndexImgs();
//        if (indexImgList == null || indexImgList.isEmpty()) {
//            return ResponseEntity.ok(Collections.EMPTY_LIST);
//        }
//        return ResponseEntity.ok(indexImgList);
//    }


    /**
     * 查询轮播图的具体信息
     *
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('admin:indexImg:info')")
    @ApiOperation("根据id查询轮播图")
    public ResponseEntity<IndexImg> info(@PathVariable("id") Long id) {
        IndexImg indexImg = indexImgService.getInfo(id);
        return ResponseEntity.ok(indexImg);
    }


    // -----------------下面是前台代码了
    // 因为微信小程序的性能比较差 基本知识  前台代码的大小必须要小于  4M
    // 性能比较差 http请求的响应的时候 处理的数据包不能过大 专门封装对象

    @GetMapping("indexImgs")
    @ApiOperation("加载前台轮播图接口")
    public ResponseEntity<List<IndexImgVo>> loadFrontIndexImg() {
        List<IndexImgVo> indexImgVos = indexImgService.findFrontIndexImg();
        return ResponseEntity.ok(indexImgVos);
    }

}
