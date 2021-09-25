package com.gohb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConfigServerController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private Integer port;

    /**
     * 刷新配置
     * git 发送请求到这里， 我们发一个请求 到config-server里面
     * @return
     */
    @PostMapping("myRefresh")
    public ResponseEntity<String> myRefresh(){
        String url = "http://localhost:" + port + "/actuator/bus-refresh";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("content-type","application/json;charset=utf-8");
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        if (responseEntity.getStatusCode().equals(HttpStatus.NO_CONTENT)){
            return ResponseEntity.ok("刷新配置成功");
        }
        return ResponseEntity.badRequest().body("刷新失败");

    }

}
