package com.gohb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.UserAddr;
import com.gohb.mapper.UserAddrMapper;
import com.gohb.service.UserAddrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
@CacheConfig(cacheNames = "com.gohb.service.impl.UserAddrServiceImpl")
public class UserAddrServiceImpl extends ServiceImpl<UserAddrMapper, UserAddr> implements UserAddrService {

    @Autowired
    private UserAddrMapper userAddrMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 根据用户的openId，
     * 查询该用户的全部收货地址
     *
     * @param openId
     * @return
     */
    @Override
    @Cacheable(key = "#openId")
    public List<UserAddr> findUserAddr(String openId) {
        List<UserAddr> userAddrs = userAddrMapper.selectList(new LambdaQueryWrapper<UserAddr>()
                .eq(UserAddr::getUserId, openId)
                .eq(UserAddr::getStatus, 1)
                .orderByAsc(UserAddr::getUpdateTime)
        );
        return userAddrs;
    }

    /**
     * 设置或修改默认收货地址
     * 修改一个数据
     * 日志要怎么记录？？
     * 记录的时候 要把之前的信息记录下来 把改后的信息记录下来 这样方便后期出了问题 做数据的回溯
     * 核心关键日志放在es 方便做数据的分析
     * 通过一个mq 把日志消息发送到 logs-service  记录es
     *
     * @param openId
     * @param id
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void changeUserDefaultAddr(String openId, Long id) {
        // 查询到之前的默认地址
        UserAddr oldAddr = userAddrMapper.selectOne(new LambdaQueryWrapper<UserAddr>()
                .eq(UserAddr::getUserId, openId)
                .eq(UserAddr::getCommonAddr, 1)
        );
        if (!ObjectUtils.isEmpty(oldAddr)) {
            // 把旧的收货地址 改一下
            oldAddr.setUpdateTime(new Date());
            oldAddr.setCommonAddr(0);
            // 插入进去
            userAddrMapper.updateById(oldAddr);
        }
        // 设置新的了
        // 查询用户新的收货地址
        UserAddr newAddr = userAddrMapper.selectById(id);
        if (ObjectUtils.isEmpty(newAddr)) {
            log.error("新的默认地址的id不存在");
            throw new IllegalArgumentException("新的默认地址的id不存在");
        }
        redisTemplate.delete("com.gohb.service.impl.UserAddrServiceImpl::" + oldAddr.getUserId());
        newAddr.setCommonAddr(1);
        newAddr.setUpdateTime(new Date());
        userAddrMapper.updateById(newAddr);
    }

    /**
     * 获取用户的默认收货地址
     * @param openId
     * @return
     */
    @Override
    public UserAddr getUserDefaultAddr(String openId) {
        UserAddr userAddr = userAddrMapper.selectOne(new LambdaQueryWrapper<UserAddr>()
                .eq(UserAddr::getUserId, openId)
                .eq(UserAddr::getCommonAddr, 1)
        );
        return userAddr;
    }


    /**
     * 新增用户收货地址
     * @param userAddr
     * @return
     */
    @Override
    @CacheEvict(key = "#userAddr.userId")
    public boolean save(UserAddr userAddr) {
        log.info("新增用户收货地址");
        userAddr.setUpdateTime(new Date());
        userAddr.setCreateTime(new Date());
        userAddr.setStatus(1);
        userAddr.setVersion(0);
        // 业务 如果用户之前没有收货地址 那么你新增的这一条就是默认收货地址
        Integer count = userAddrMapper.selectCount(new LambdaQueryWrapper<UserAddr>()
                .eq(UserAddr::getUserId, userAddr.getUserId())
                .eq(UserAddr::getCommonAddr, 1)
        );
        if (count <= 0) {
            // 之前就没有 我们就设置当前新增的是默认收货地址
            userAddr.setCommonAddr(1);
        }
        return userAddrMapper.insert(userAddr) > 0;
    }

    /**
     * 删除用户的一个收货地址
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        log.info("删除用户{}的收货地址", id);
        UserAddr userAddr = getById(id);
        if (userAddr == null) {
            throw new IllegalArgumentException("删除用户的地址为空");
        }
        String userId = userAddr.getUserId();
        redisTemplate.delete("com.gohb.service.impl.UserAddrServiceImpl::" + userId);
        return super.removeById(id);
    }

    /**
     * 更新用户收货地址
     * @param userAddr
     * @return
     */
    @Override
    @CacheEvict(key = "#userAddr.userId")
    public boolean updateById(UserAddr userAddr) {
        userAddr.setUpdateTime(new Date());
        return super.updateById(userAddr);
    }

}
