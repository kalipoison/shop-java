package com.gohb.controller;

import com.gohb.domain.Area;
import com.gohb.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/area")
@Api(tags = "区域管理")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('admin:area:list')")
    @ApiOperation("查询地址的列表")
    public ResponseEntity<List<Area>> list() {
        List<Area> areas = areaService.list();
        return ResponseEntity.ok(areas);
    }

    @GetMapping("/listByPid")
    @PreAuthorize("hasAuthority('admin:area:list')")
    @ApiOperation("通过父区域id 查询子区域")
    public ResponseEntity<List<Area>> listByPid(@RequestParam(name = "pid", defaultValue = "0")
                                                        Long pId) {
        List<Area> areas = areaService.listByPid(pId);
        return ResponseEntity.ok(areas);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:area:save')")
    @ApiOperation("新增一个Area")
    public ResponseEntity<Void> addArea(@RequestBody @Validated Area area) {
        areaService.save(area);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('admin:area:update')")
    @ApiOperation("根据id 修改Area")
    public ResponseEntity<Void> update(@RequestBody @Validated Area area) {
        areaService.updateById(area);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:area:delete')")
    @ApiOperation("删除Area")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        areaService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('admin:area:info')")
    @ApiOperation("查询Area 详情")
    public ResponseEntity<Area> info(@PathVariable("id") Long id) {
        Area entity = areaService.getById(id);
        return ResponseEntity.ok(entity);
    }

}
