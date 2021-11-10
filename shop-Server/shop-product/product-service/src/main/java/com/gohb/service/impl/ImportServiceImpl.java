//package com.gohb.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.rabbitmq.client.Channel;
//import com.gohb.constant.QueueConstant;
//import com.gohb.dao.ProdEsDao;
//import com.gohb.domain.Prod;
//import com.gohb.es.ProdEs;
//import com.gohb.service.ImportService;
//import com.gohb.service.ProdService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Service
//@Slf4j
//public class ImportServiceImpl implements ImportService, CommandLineRunner {
//
//    @Autowired
//    private ProdEsDao prodEsDao;
//
//    @Autowired
//    private ProdService prodService;
//
//    /**
//     * 每次导入的条数
//     */
//    @Value("${esimport.size}")
//    private Integer size;
//
//    /**
//     * 给一个初始时间
//     */
//    private Date t1;
//
//
//    /**
//     * 全量导入 发生在项目一起动
//     * mysql的prod导入es
//     */
//    @Override
//    public void importAll() {
//        log.info("开始全量导入");
//        // 计算分页
//        // 1. 查询所有的count
//        Integer totalCount = prodService.getTotalCount(null, null);
//        if (totalCount == null || totalCount == 0) {
//            log.info("根本就没有数据让我们导入");
//            return;
//        }
//        // 2. 计算分页 totalCount / size
//        long page = totalCount % size == 0 ? totalCount / size : ((totalCount / size) + 1);
//        // 循环
//        for (int i = 1; i <= page; i++) {
//            // 分页查询导入
//            importToEs(i, size, null, null);
//        }
//        log.info("全量导入完成");
//        // 记录一个时间 初始时间 9.20
//        t1 = new Date();
//    }
//
//    /**
//     * 导入es的方法
//     *
//     * @param current
//     * @param size
//     * @param t1
//     * @param t2
//     */
//    private void importToEs(int current, Integer size, Date t1, Date t2) {
//        // 1.分页查询
//        // 组装一个page  如果让他做分页 查询总条数 小小的优化
//        Page<Prod> page = new Page<>(current, size, false);
//        Page<Prod> prodPage = prodService.findProdByPageToEs(page, t1, t2);
//        List<Prod> prodList = prodPage.getRecords();
//        // new  一个空的集合 专门放导入es的对象
//        ArrayList<ProdEs> prodEsList = new ArrayList<>(prodList.size() * 2);
//        prodList.forEach(prod -> {
//            ProdEs prodEs = new ProdEs();
//            //  对象copy 只要是属性名称相同就可以拷贝
//            BeanUtil.copyProperties(prod, prodEs, true);
//
//            // 放入new的空集合
//            prodEsList.add(prodEs);
//        });
//        // 批量导入es
//        prodEsDao.saveAll(prodEsList);
//    }
//
//    /**
//     * 增量导入 定时任务
//     * 商家商家一个商品 定时任务30min  查询mysql 如果有修改的 查出来 导入到es（和es做同步）
//     * initialDelay 项目启动完以后 首次执行定时任务的时间
//     * fixedRate 执行完第一次任务后每次间隔多长时间
//     */
//    @Override
//    @Scheduled(initialDelay = 3 * 60 * 1000, fixedRate = 3 * 60 * 1000)
//    public void importUpdate() {
//        log.info("增到导入开始");
//        // 这个t2 比t1 大30分钟  9.50
//        Date t2 = new Date();
//        log.error("t1是{}，t2是{}", t1, t2);
//        // 也要分页
//        Integer totalCount = prodService.getTotalCount(t1, t2);
//        if (totalCount == null || totalCount == 0) {
//            log.info("根本就没有数据让我们导入");
//            // 把时间窗口锁定
//            t1 = t2;
//            return;
//        }
//        // 2. 计算分页 totalCount / size
//        long page = totalCount % size == 0 ? totalCount / size : ((totalCount / size) + 1);
//        // 循环
//        for (int i = 1; i <= page; i++) {
//            // 分页查询导入
//            importToEs(i, size, t1, t2);
//        }
//        // 优化
//        // 代码走到这里来了   9.40   9.20  10.20
//        t1 = t2;
//        log.info("增到导入结束");
//    }
//
//    /**
//     * 定义一个空方法
//     */
//    @Override
//    public void quickImport() {
//
//    }
//
//    /**
//     * 快速导入 用户下单减库存了 mq实现
//     * 用户下单 有一个 order-service  我们做一个解耦合
//     * 用户下订单量会特别大 如果使用 openfeign 对服务压力很大 性能不好
//     * 使用mq  特点：异步，削峰，限流，解耦合
//     * 业务流程：
//     * 1. 用户下订单  会修改库存  mysql  同步es
//     * 2. order-service 把减库存的消息放入 mq   prodId  count  Map<Long,Integer>
//     * 3. 我们处理消息
//     */
//    @RabbitListener(queues = QueueConstant.PROD_CHANGE_QUEUE, concurrency = "3-5")
//    public void quickImportByMq(Message message, Channel channel) {
//        // 拿到消息体
//        String msgStr = new String(message.getBody());
//        // 把数据转成map集合
//        JSONObject prodInfo = JSON.parseObject(msgStr);
//        // 消费消息
//        ArrayList<Long> prodIds = new ArrayList<>();
//        prodInfo.forEach((prodId, count) -> {
//            // 根据id 查询es 查到要修改的对象
//            prodIds.add(Long.valueOf(prodId));
//        });
//        // 查询es
//        Iterable<ProdEs> prodChangeList = prodEsDao.findAllById(prodIds);
//        // 循环 修改数据了
//        prodChangeList.forEach(prodEs -> {
//            Long prodId = prodEs.getProdId();
//            // 改变的数量
//            Integer count = prodInfo.getInteger(String.valueOf(prodId));
//            // 操作数量的改变  方便做增加和减少的
//            long finalCount = prodEs.getTotalStocks() + count;
//            if (finalCount < 0) {
//                log.error("修改es的库存小于0了");
//                throw new IllegalArgumentException(prodId + "的es库存小于0");
//            }
//            prodEs.setTotalStocks(finalCount);
//        });
//        // 插入es
//        prodEsDao.saveAll(prodChangeList);
//        // 签收消息
//        long deliveryTag = message.getMessageProperties().getDeliveryTag();
//        try {
//            channel.basicAck(deliveryTag, false);
//            log.info("同步es成功");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 当ioc容器初始化完成 当springboot启动完成 就会执行下面的方法
//     *
//     * @param args
//     * @throws Exception
//     */
//    @Override
//    public void run(String... args) throws Exception {
//        importAll();
//    }
//}
