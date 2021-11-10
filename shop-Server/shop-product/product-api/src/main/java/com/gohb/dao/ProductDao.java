package com.gohb.dao;

import com.gohb.es.ProdEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao extends ElasticsearchRepository<ProdEs, Long> {

}
