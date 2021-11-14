package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.gohb.constant.CategoryConstant;
import com.gohb.constant.ProductConstant;
import com.gohb.domain.Category;
import com.gohb.domain.Prod;
import com.gohb.mapper.CategoryMapper;
import com.gohb.mapper.ProdMapper;
import com.gohb.service.CategoryService;
import com.gohb.utils.FastDFSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = "com.gohb.service.impl.CategoryServiceImpl")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProdMapper prodMapper;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;


    /**
     * 查询所有的商品分类，存放到redis 中
     *
     * @return
     */
    @Cacheable(key = ProductConstant.ALL_CATEGORIES)
    @Override
    public List<Category> list() {
        List<Category> list = super.list();
        return list;
    }


    /**
     * 新增商品分类
     * 删除所有相关缓存 不然出现脏读
     *
     * @param category
     * @return
     */
    @Override
    @Caching(evict = {
            @CacheEvict(key = ProductConstant.ALL_CATEGORIES),
            @CacheEvict(key = ProductConstant.SUB_CATEGORIES)
    })
    public boolean save(Category category) {
        log.info("新增一个分类开始,名称为{}", JSON.toJSON(category));
        //校验
        validate(category);
        category.setRecTime(new Date());
        category.setUpdateTime(new Date());
        category.setStatus(1);
        category.setShopId(1L);
        category.setSeq(1);
        return super.save(category);
    }

    /**
     * 校验
     *
     * @param category
     */
    private void validate(Category category) {
        if (category.getParentId() == null || category.getParentId().equals(0L)) {
            //说明新增的是一个父节点
            category.setParentId(0L);
            //分类层级第一级别
            category.setGrade(1);
        } else {
            //查询一下父id 有没有值
            Category parent = this.getById(category.getParentId());
            if (ObjectUtils.isEmpty(parent)) {
                throw new IllegalArgumentException("新增分类时选的父节点不能为空");
            }
            category.setParentId(parent.getCategoryId());
            //这里设置级别
            int subNode = parent.getGrade() + 1;
            category.setGrade(subNode);
        }
    }


    /**
     * 删除一个分类
     * 删除所有相关缓存 不然出现脏读
     * @param id
     * @return
     */
    @Caching(evict = {
            @CacheEvict(key = CategoryConstant.CATEGORY_ROOT_PREFIX),
            @CacheEvict(key = CategoryConstant.CATEGORY_PREFIX)
    })
    @Override
    public boolean removeById(Serializable id) {
        log.info("删除商品分类的id 为{}", id);
        //判断有没有子分类
        Integer count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, id)
        );
        if (count > 0) {
            //说明有子分类，不能删除
            throw new IllegalArgumentException("该分类有子分类，不能删除");
        }
        //查询有没有商品再用此分类
        Integer count1 = prodMapper.selectCount(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getCategoryId, id)
        );
        if (count1 > 0) {
            throw new IllegalArgumentException("有商品处于该分类中，不能删除");
        }
        return super.removeById(id);
    }

    /**
     * 删除所有相关缓存
     *
     * @param entity
     * @return
     */
    @Caching(evict = {
            @CacheEvict(key = CategoryConstant.CATEGORY_ROOT_PREFIX),
            @CacheEvict(key = CategoryConstant.CATEGORY_PREFIX)
    })
    @Override
    public boolean updateById(Category entity) {
        log.info("修改分类开始，被修改的id 为{}", entity.getCategoryId());
        //需改注意图片，先得到原来的独享
        Category oldCateGory = this.getById(entity.getCategoryId());
        if (ObjectUtils.isEmpty(oldCateGory)) {
            throw new IllegalArgumentException("修改的值不存在");
        }
        @NotBlank String pic = oldCateGory.getPic();
        //看一下图片
        if (!pic.equals(entity.getPic())) {
            //如果图片被修改了，则从fastDFS 里删掉
            String group = FastDFSUtil.parseGroup(pic);
            try {
                fastFileStorageClient.deleteFile(group, pic.replaceFirst(group + "/", ""));
            } catch (Exception e) {
                log.error("图片删除失败,地址为{}", pic);
            }
        }
        return super.updateById(entity);
    }

    /**
     * 查询商品的分类所有父节点
     *
     * @return
     */
    @Override
    @Cacheable(key = ProductConstant.SUB_CATEGORIES)
    public List<Category> listAllParent() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, 0L)
        );
    }

}
