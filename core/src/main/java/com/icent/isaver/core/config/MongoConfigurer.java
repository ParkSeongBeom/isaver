package com.icent.isaver.core.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

/**
 * MongoDatabase Configurer
 *
 * @author : psb
 * @version : 1.0
 * </pre>
 */
@Configuration
@EnableMongoRepositories
public class MongoConfigurer extends AbstractMongoClientConfiguration {

    @Value("${db.mongo.host}")
    private String host = null;

    @Value("${db.mongo.port}")
    private Integer port = null;

    @Value("${db.mongo.username}")
    private String username = null;

    @Value("${db.mongo.password}")
    private String password = null;

    @Value("${db.mongo.database}")
    private String database = null;

    @Override
    protected String getDatabaseName(){
        return database;
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(setMongoClientSettings());
    }

    private MongoClientSettings setMongoClientSettings() {
        MongoCredential credential = MongoCredential.createCredential(username,database,password.toCharArray());
        return MongoClientSettings.builder()
                .credential(credential)
                .applyToClusterSettings(builder ->
                        builder.hosts(Arrays.asList(new ServerAddress(host, port))))
                .build();
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception{
        return new MongoTemplate(mongoClient(),database);
    }
}
