package com.gohb.controller;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Api(tags = "文件上传的接口")
@RestController
@RequestMapping("/admin/file")
public class FileController {


    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Value("${resources.url}")
    private String host;


    @PostMapping("/upload/element")
    @ApiOperation("文件上传的接口")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) {
        if (ObjectUtils.isEmpty(file)) {
            return ResponseEntity.ok().build();
        }
        // 返回文件路径 http://192.168.127.128/groupxxxxx
        String fullPath = null;
        try {
            fullPath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), "jpg", null).getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fullPath);
        return ResponseEntity.ok(host + "/" + fullPath);
    }

}