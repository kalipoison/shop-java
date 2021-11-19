package com.gohb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gohb.dao.ProductDao;
import com.gohb.es.ProdEs;
import com.gohb.service.SearchService;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 武汉尚学堂
 */
@Service
public class SearchServiceImpl implements SearchService {


    // 复杂查询
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ProductDao productDao;


    /**
     * 根据标签分组查询
     *
     * @param tagId
     * @param current
     * @param size
     * @return
     */
    @Override
    public Page<ProdEs> findProdByTagId(Long tagId, Integer current, Integer size) {
        // 构建查询条件
        TermQueryBuilder termQuery = QueryBuilders.termQuery("tagList", tagId);
        // 自己组装page  可能要改成1
        Page<ProdEs> page = new Page<>(current, size);
        return query(termQuery, page, null, null);
    }


    /**
     * 根据关键字查询
     *
     * @param prodName
     * @param current
     * @param size
     * @param sort
     * @return
     */
    @Override
    public Page<ProdEs> findProdByKeywords(String prodName, Integer current, Integer size, Integer sort) {
        MatchQueryBuilder matchQuery = null;
        if (!StringUtils.isEmpty(prodName)) {
            matchQuery = QueryBuilders.matchQuery("prodName", prodName);
        }
        Page<ProdEs> page = new Page<>(current, size);
        return query(matchQuery, page, prodName, sort);
    }

    /**
     * 排序的方法
     *
     * @param sort
     * @return
     */
    private SortBuilder doSort(Integer sort) {
        switch (sort) {
            case 0:
                // 好评
                return SortBuilders.fieldSort("positiveRating").order(SortOrder.DESC);
            case 1:
                // 销量
                return SortBuilders.fieldSort("soldNum").order(SortOrder.DESC);
            case 2:
                // 价格
                return SortBuilders.fieldSort("price").order(SortOrder.ASC);
            default:
                throw new IllegalArgumentException("非法的排序");
        }
    }


    /**
     * 执行查询和返回的 方法
     *
     * @return
     */
    public Page<ProdEs> query(AbstractQueryBuilder abstractQueryBuilder, Page<ProdEs> page, String keywords, Integer sort) {
        // 组装查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of((int) page.getCurrent() - 1, (int) page.getSize()));
        if (abstractQueryBuilder != null) {
            queryBuilder.withQuery(abstractQueryBuilder);
        }
        // 专门做排序
        if (sort != null) {
            // jvm 底层做了优化的  null 放前面 消耗一个操作单元
            SortBuilder sortBuilder = doSort(sort);
            queryBuilder.withSort(sortBuilder);
        }
        if (keywords != null) {
            // 不高亮
            HighlightBuilder.Field field = new HighlightBuilder.Field("prodName");
            field.preTags("<span style='color:red'>");
            field.postTags("</span>");
            queryBuilder.withHighlightFields(field);
        }
        // 返回的集合值
        ArrayList<ProdEs> prodEs = new ArrayList<>();
        // 做搜索了
        SearchHits<ProdEs> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), ProdEs.class);
        // 处理高亮
        searchHits.forEach(searchHit -> {
            ProdEs prod = searchHit.getContent();
//            if (keywords != null) {
//                List<String> prodNameHighLight = searchHit.getHighlightField("prodName");
//                if (!CollectionUtils.isEmpty(prodNameHighLight)) {
//                    //
//                    String newName = prodNameHighLight.get(0);
//                    prod.setProdName(newName);
//                }
//            }
            prodEs.add(prod);
        });
        page.setTotal(searchHits.getTotalHits());
        page.setRecords(prodEs);
        return page;
    }


    /**
     * 提供远程调用根据ids查询商品信息
     *
     * @param prodIds
     * @return
     */
    @Override
    public List<ProdEs> findProdByIds(List<Long> prodIds) {
        List<ProdEs> prodEsList = (List<ProdEs>) productDao.findAllById(prodIds);
        return prodEsList;
    }

    /**
     * 根据分类id分页查询商品
     *
     * @param cateGoryId
     * @param current
     * @param size
     * @return
     */
    @Override
    public Page<ProdEs> findProdByCategoryId(Long cateGoryId, Integer current, Integer size) {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("categoryId", cateGoryId);
        Page<ProdEs> page = new Page<>(current, size);
        return query(termQueryBuilder, page, null, null);
    }
}
