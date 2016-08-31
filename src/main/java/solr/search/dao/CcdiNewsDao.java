package solr.search.dao;

import solr.search.domain.News;

import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public interface CcdiNewsDao {
    void save(News news);

    List<News> findAll();
}
