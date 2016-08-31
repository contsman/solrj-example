package solr.search.dao;

import solr.search.domain.Product;

import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public interface JDProductDao {
    void save(Product product);

    List<Product> findAll();
}