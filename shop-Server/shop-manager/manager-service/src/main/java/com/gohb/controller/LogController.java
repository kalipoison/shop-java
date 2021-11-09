package com.gohb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.domain.SysLog;
import com.gohb.service.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/log")
@Api(tags = "日志的管理接口")
public class LogController {

    @Autowired
    private SysLogService sysLogService;

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public ResponseEntity<IPage<SysLog>> page(Page<SysLog> page, SysLog sysLog) {
        //设置排序
        page.addOrder(OrderItem.desc("create_date"));
        Page<SysLog> logPage = sysLogService.page(page, new LambdaQueryWrapper<SysLog>()
                .like(StringUtils.hasText(sysLog.getUsername()), SysLog::getUsername, sysLog.getUsername())
                .like(StringUtils.hasText(sysLog.getOperation()), SysLog::getOperation, sysLog.getOperation())
        );
        return ResponseEntity.ok(logPage);
    }
}
