package com.gohb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.dao.ProductDao;
import com.gohb.domain.Prod;
import com.gohb.es.ProdEs;
import com.gohb.service.ImportService;
import com.gohb.service.ProdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ImportServiceImpl implements ImportService, CommandLineRunner {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProdService prodService;

    /**
     * 每次导入多少条
     */
    @Value("${esimport.size}")
    private Integer size;

    /**
     * 全量导入 发生在项目一起动
     * mysql的prod导入es
     */
    @Override
    public void importAll() {
        log.info("开始全量导入");
        // 计算分页
        // 1.查询所有的count
        Integer totalCount = prodService.getTotalCount(null, null);
        if (totalCount == null || totalCount == 0) {
            log.info("根本就没有数据让我们导入");
            return;
        }
        // 2.计算分页 totalCount / size
        long page = totalCount % size == 0 ? totalCount / size : ((totalCount / size) + 1);
        for (int i = 1 ; i <= page ; i++){
            // 查询导入
            log.info("分页查询数据库i=" + i);
            importToEs(i, size, null, null);
        }
    }

    /**
     * 导入ES的方法
     * @param current
     * @param size
     * @param t1
     * @param t2
     */
    private void importToEs(int current, Integer size, Date t1, Date t2) {
        // 1. 分页查询
        // 组装page 如果让它做分页，查询总条数
        Page<Prod> page = new Page(current, size, false);
        Page<Prod> prodPage = prodService.findProdByPageToEs(page, t1, t2);
        List<Prod> prodList = prodPage.getRecords();
        // new  一个空的集合 专门放导入es的对象
        List<ProdEs> prodEsList = new ArrayList<>();
        prodList.forEach(prod -> {
            ProdEs prodEs = new ProdEs();
            // 对象拷贝
            BeanUtil.copyProperties(prod, prodEs, true);
            // 放入new的集合
            prodEsList.add(prodEs);
        });
        productDao.saveAll(prodEsList);

    }

    /**
     * 增量导入 定时任务
     * 商家商家一个商品 定时任务30min  查询mysql 如果有修改的 查出来 导入到es（和es做同步）
     * initialDelay 项目启动完以后 首次执行定时任务的时间
     * fixedRate 执行完第一次任务后每次间隔多长时间
     */
    @Override
    public void importUpdate() {

    }

    /**
     * 定义一个空方法
     */
    @Override
    public void quickImport() {

    }

    /**
     * 当ioc容器初始化完成 当springboot启动完成 就会执行下面的方法
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        importAll();
    }
}
