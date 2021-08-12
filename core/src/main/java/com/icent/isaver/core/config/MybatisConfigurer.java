package com.icent.isaver.core.config;

import com.icent.isaver.core.util.ResourceFinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Mybatis Configurer
 *
 * @author : psb
 * </pre>
 */
@Slf4j
@Configuration
@MapperScan(
        basePackages = {"com.icent.isaver.core.dao.postgresql"}
        ,sqlSessionFactoryRef = "sqlSessionFactory"
)
public class MybatisConfigurer {
    @Resource(name = "dataSource")
    private DataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory(){
        SqlSessionFactoryBean sqlSessionFactoryBean = null;
        SqlSessionFactory sqlSessionFactory = null;
        sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(ResourceFinder.getResources("mybatis/mapper/", "*.xml"));
        sqlSessionFactoryBean.setTypeAliases(ResourceFinder.getClassesArray("com/icent/isaver/core/bean"));
        try {
            sqlSessionFactory = sqlSessionFactoryBean.getObject();
        } catch (Exception e) {
            sqlSessionFactory = null;
            log.error(e.getMessage());
        }
        return sqlSessionFactory;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(){
        return new SqlSessionTemplate(sqlSessionFactory());
    }

    @Bean
    public DataSourceTransactionManager txManager(){
        DataSourceTransactionManager txManager = new DataSourceTransactionManager();
        txManager.setDataSource(dataSource);
        return txManager;
    }
}
