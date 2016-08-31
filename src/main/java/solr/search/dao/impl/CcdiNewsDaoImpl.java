package solr.search.dao.impl;

import solr.search.dao.CcdiNewsDao;
import solr.search.domain.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
@Repository
public class CcdiNewsDaoImpl implements CcdiNewsDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<News> findAll() {
        return jdbcTemplate.query("select * from news", new RowMapper<News>() {
            @Override
            public News mapRow(ResultSet rs, int rowNum) throws SQLException {
                News news = new News();
                news.setId(rs.getString("id"));
                news.setTitle(rs.getString("title"));
                news.setContent(rs.getString("content"));
                news.setUrl(rs.getString("url"));
                news.setSource(rs.getString("source"));
                news.setPubdate(rs.getString("pubdate"));
                news.setCreate(rs.getDate("create"));
                news.setUpdate(rs.getDate("update"));
                return news;
            }
        });
    }

    public void save(final News news) {
        String sql = "INSERT INTO news (title,content,url,source,pubdate,`create`,`update`) VALUES (?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, news.getTitle());
                ps.setString(2, news.getContent());
                ps.setString(3, news.getUrl());
                ps.setString(4, news.getSource());
                ps.setString(5, news.getPubdate());
                ps.setTimestamp(6, new Timestamp(news.getCreate().getTime()));
                ps.setTimestamp(7, new Timestamp(news.getUpdate().getTime()));
            }
        });
    }
}
