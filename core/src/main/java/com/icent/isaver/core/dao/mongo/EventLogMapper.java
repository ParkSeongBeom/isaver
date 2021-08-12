package com.icent.isaver.core.dao.mongo;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class EventLogMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertOne(Map event){
        mongoTemplate.getCollection("eventLog").insertOne(new Document(event));
    }
}
