package com.icent.isaver.core.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Persistence Configurer
 *
 * @author : psb
 * @version : 1.0
 * </pre>
 */
@Configuration
public class PersistenceConfigurer {

    @Value("${db.postgres.driver}")
    private String driver = null;

    @Value("${db.postgres.url}")
    private String url = null;

    @Value("${db.postgres.name}")
    private String name = null;

    @Value("${db.postgres.password}")
    private String password = null;

    @Value("${db.postgres.validateQuery}")
    private String validateQuery = null;

    @Value("${db.postgres.maxconnect}")
    private String maxconnect = null;

    @Value("${db.postgres.minconnect}")
    private String minconnect = null;

    @Value("${db.postgres.wait}")
    private String dbWait = null;

    @Bean
    public DataSource dataSource(){
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(name);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validateQuery);

        int maxConnect = Integer.valueOf(maxconnect);
        int minConnect = Integer.valueOf(minconnect);
        int wait = Integer.valueOf(dbWait);

        dataSource.setDefaultAutoCommit(false);
        dataSource.setInitialSize(minConnect);
        dataSource.setMaxTotal(maxConnect);
        dataSource.setMinIdle(minConnect);
        dataSource.setMaxIdle(maxConnect);
        dataSource.setMaxWaitMillis(wait);
        return dataSource;
    }
}
