package com.gohb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.ProdComm;
import com.gohb.domain.ProdCommSxt;
import com.gohb.service.ProdCommService;
import com.gohb.vo.ProdCommVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prod/prodComm")
@Api(tags = "商品评论管理")
public class ProdCommController {

    @Autowired
    private ProdCommService prodCommService;

    @GetMapping("page")
    @ApiOperation("分页查询评论")
    @PreAuthorize("hasAuthority('prod:prodComm:page')")
    public ResponseEntity<IPage<ProdComm>> loadProdCommByPage(Page<ProdComm> page, ProdComm prodComm) {
        IPage<ProdComm> prodCommIPage = prodCommService.loadProdCommByPage(page, prodComm);
        return ResponseEntity.ok(prodCommIPage);
    }

    /**
     * 需要返回什么数据
     * 总评多少个
     * 好评多少个
     * 好频率多少
     * 中评多少个
     * 差评多少个
     * 带图的多少个
     *
     * @param prodId
     * @return
     */
    @GetMapping("prodComm/prodCommData")
    @ApiOperation("根据商品id 查询商品评论总览")
    public ResponseEntity<ProdCommVo> getProdCommAll(@RequestParam("prodId") Long prodId) {
        ProdCommVo prodCommVo = prodCommService.getProdCommAll(prodId);
        return ResponseEntity.ok(prodCommVo);
    }

    @GetMapping("prodComm/prodCommPageByProd")
    @ApiOperation("分页查询前台的评论")
    public ResponseEntity<IPage<ProdCommSxt>> prodCommPageByProd(Page<ProdComm> page, ProdComm prodComm) {
        IPage<ProdCommSxt> prodCommSxtIPage = prodCommService.prodCommPageByProd(page, prodComm);
        return ResponseEntity.ok(prodCommSxtIPage);
    }

    @GetMapping("{id}")
    @ApiOperation("获得一条评论数据")
    @PreAuthorize("hasAuthority('prod:prodComm:info')")
    public ResponseEntity<ProdCommVo> findById(@PathVariable("id") Long id) {
        ProdCommVo byId = prodCommService.getProdCommById(id);
        return ResponseEntity.ok().body(byId);
    }

    @PutMapping
    @ApiOperation("评论的修改")
    @PreAuthorize("hasAuthority('prod:prodComm:update')")
    public ResponseEntity<Void> update(@RequestBody @Validated ProdComm prodComm) {
        this.prodCommService.updateById(prodComm);
        return ResponseEntity.ok().build();
    }


}
