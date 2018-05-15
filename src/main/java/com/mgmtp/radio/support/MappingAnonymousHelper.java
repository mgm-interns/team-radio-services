package com.mgmtp.radio.support;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class MappingAnonymousHelper {

    private final MongoTemplate mongoTemplate;

    public MappingAnonymousHelper(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void updateUserId(String anonymousId, String userId, String entityKey, Class<?> entityClass ) {
        Query query = new Query();
        query.addCriteria(new Criteria(entityKey).is(anonymousId));
        Update update = new Update();
        update.set(entityKey, userId);
        mongoTemplate.updateMulti(query, update, entityClass);
    }
}
