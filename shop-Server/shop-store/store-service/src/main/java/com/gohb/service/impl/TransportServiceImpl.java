package com.gohb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.*;
import com.gohb.mapper.*;
import com.gohb.service.TransportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = "com.gohb.service.impl.TransportServiceImpl")
public class TransportServiceImpl extends ServiceImpl<TransportMapper, Transport> implements TransportService{

    @Autowired
    private TransportMapper transportMapper;

    @Autowired
    private TransfeeMapper transfeeMapper;

    @Autowired
    private TransfeeFreeMapper transfeeFreeMapper;

    @Autowired
    private TranscityMapper transcityMapper;

    @Autowired
    private TranscityFreeMapper transcityFreeMapper;

    @Autowired
    private AreaMapper areaMapper;

    /**
     * 分页查询运费模板
     *
     * @param page
     * @param condition
     * @return
     */
    @Override
    public IPage<Transport> findTransportPage(Page<Transport> page, Transport condition) {
        page.addOrder(OrderItem.desc("create_time"));
        return transportMapper.selectPage(page, new LambdaQueryWrapper<Transport>().
                like(StringUtils.hasText(condition.getTransName()), Transport::getTransName,
                        condition.getTransName()));
    }
    @Override
    @Cacheable(key = "#id")
    @Transactional
    public Transport getById(Serializable id) {
        log.info("查询id 为{}的运费模板", id);
        //先查出来再查对应的收费模板和免费模板
        Transport transport = super.getById(id);
        //查询收费的
        transport.setTransfees(getTransFees(transport.getTransportId()));
        //查询免费的
        transport.setTransfeeFrees(getTransFeesFree(transport.getTransportId()));
        return super.getById(id);
    }


    /**
     * 查询免费的模板
     *
     * @param transportId
     * @return
     */
    private List<TransfeeFree> getTransFeesFree(Long transportId) {
        //查询免费模板
        List<TransfeeFree> transfeeFrees = transfeeFreeMapper.selectList(new
                LambdaQueryWrapper<TransfeeFree>()
                .eq(TransfeeFree::getTransportId, transportId)
        );
        if (ObjectUtils.isEmpty(transfeeFrees)) {
            return Collections.emptyList();
        }
        transfeeFrees.forEach(transfeeFree -> {
            //循环查询免费的城市
            List<Object> cityIds = transcityFreeMapper.selectObjs(new
                    LambdaQueryWrapper<TranscityFree>()
                    .select(TranscityFree::getFreeCityId)
                    .eq(TranscityFree::getTransfeeFreeId, transfeeFree.getTransfeeFreeId())
            );
            if (!ObjectUtils.isEmpty(cityIds)) {
                //如果城市不等空
                List<Area> areas = areaMapper.selectList(new LambdaQueryWrapper<Area>()
                        .in(Area::getAreaId, cityIds)
                );
                transfeeFree.setFreeCityList(areas);
            }
        });
        return transfeeFrees;
    }

    /**
     * 查询所有收费的模板
     *
     * @param transportId
     * @return
     */
    private List<Transfee> getTransFees(Long transportId) {
        //先查询收费的模板
        List<Transfee> transfees = transfeeMapper.selectList(new
                LambdaQueryWrapper<Transfee>()
                .eq(Transfee::getTransportId, transportId)
        );
        if (ObjectUtils.isEmpty(transfees)) {
            return Collections.emptyList();
        }
        //循环查询收费模板对应的城市
        transfees.forEach(transfee -> {
            List<Object> cityIds = transcityMapper.selectObjs(new
                    LambdaQueryWrapper<Transcity>()
                    .select(Transcity::getCityId)
                    .eq(Transcity::getTransfeeId, transfee.getTransfeeId())
            );
            if (!ObjectUtils.isEmpty(cityIds)) {
                //如果城市不为空，查询城市
                List<Area> areas = areaMapper.selectList(new LambdaQueryWrapper<Area>()
                        .in(Area::getAreaId, cityIds)
                );
                //设置对应模板的城市
                transfee.setCityList(areas);
            }
        });
        return transfees;
    }

    @Override
    @Transactional
    public boolean save(Transport entity) {
        //新增一个运费模板
        boolean save = super.save(entity);
        if (save) {
            //处理包邮的
            handlerFree(entity);
            //处理收费的
            handlerFee(entity);
        }
        return save;
    }

    @Transactional
    @CacheEvict(key = "#entity.transportId")
    public boolean updateById(Transport entity) {
        boolean flag = removeById(entity.getTransportId());
        if (flag) {
            save(entity);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean removeById(Serializable id) {
        boolean b = super.removeById(id); // transport
        if (b) {
            //删除收费的
            deleteFee(id);
            //删除包邮的
            deleteFree(id);
        }
        return b;
    }

    private void deleteFee(Serializable id) {
        List<Object> transCityIds = transfeeMapper.selectObjs(new
                LambdaQueryWrapper<Transfee>().
                select(Transfee::getTransfeeId).
                eq(Transfee::getTransportId, id));
        if (transCityIds != null && !transCityIds.isEmpty()) {
            // 删除和城市的中间表
            transcityMapper.delete(new
                    LambdaQueryWrapper<Transcity>().in(Transcity::getTransfeeId, transCityIds));
        }
        // 删除自己
        transfeeMapper.delete(new LambdaQueryWrapper<Transfee>().
                eq(Transfee::getTransportId, id));
    }

    private void deleteFree(Serializable id) {
        List<Object> transCityFreeIds = transfeeFreeMapper.selectObjs(new
                LambdaQueryWrapper<TransfeeFree>().
                select(TransfeeFree::getTransfeeFreeId).
                eq(TransfeeFree::getTransportId, id));
        if (transCityFreeIds != null && !transCityFreeIds.isEmpty()) {
            // 删除和城市的中间表
            transcityFreeMapper.delete(new
                    LambdaQueryWrapper<TranscityFree>().in(TranscityFree::getTransfeeFreeId, transCityFreeIds));
        }
        // 删除自己
        transfeeFreeMapper.delete(new LambdaQueryWrapper<TransfeeFree>().
                eq(TransfeeFree::getTransportId, id));
    }

    /**
     * 处理收费的
     *
     * @param entity
     */
    private void handlerFee(Transport entity) {
        if (entity.getIsFreeFee().equals(1)) {
            //得到收费对象
            Transfee transfee = entity.getTransfees().get(0);
            //设置id
            transfee.setTransportId(entity.getTransportId());
            transfeeMapper.insert(transfee);
            return;
        }
        List<Transfee> transfees = entity.getTransfees(); // 所有的收费的情况
        if (!transfees.isEmpty()) {
            for (Transfee transfee : transfees) {
                insertTransFee(entity, transfee);
            }
        }
    }

    private void insertTransFee(Transport entity, Transfee transfee) {
        transfee.setTransportId(entity.getTransportId());
        int insert = transfeeMapper.insert(transfee);
        if (insert > 0) {
            List<Area> cityList = transfee.getCityList();
            if (!cityList.isEmpty()) {
                for (Area area : cityList) {
                    Transcity transcity = new Transcity();
                    transcity.setCityId(area.getAreaId());
                    transcity.setTransfeeId(transfee.getTransfeeId());
                    transcityMapper.insert(transcity);
                }
            }
        }
    }

    /**
     * 处理免费的
     *
     * @param entity
     */
    private void handlerFree(Transport entity) {
        if (entity.getIsFreeFee().equals(1)) { //买家包邮
            return;
        }
        // 条件包邮
        List<TransfeeFree> transfeeFrees = entity.getTransfeeFrees();
        if (!transfeeFrees.isEmpty()) {
            for (TransfeeFree transfeeFree : transfeeFrees) {
                insertTransFeeFree(entity, transfeeFree);
            }
        }
    }

    private void insertTransFeeFree(Transport entity, TransfeeFree transfeeFree) {
        transfeeFree.setTransportId(entity.getTransportId());
        int insert = transfeeFreeMapper.insert(transfeeFree);
        if (insert > 0) {
            List<Area> freeCityList = transfeeFree.getFreeCityList();
            if (!freeCityList.isEmpty()) {
                for (Area area : freeCityList) {
                    TranscityFree transcityFree = new TranscityFree();
                    transcityFree.setFreeCityId(area.getAreaId());
                    transcityFree.setTransfeeFreeId(transfeeFree.getTransfeeFreeId().longValue());
                    transcityFreeMapper.insert(transcityFree);
                }
            }
        }
    }


}
