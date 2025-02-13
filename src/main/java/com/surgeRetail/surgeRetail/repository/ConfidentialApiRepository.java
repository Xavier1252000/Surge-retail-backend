package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.userAndRoles.ClientSecret;
import com.surgeRetail.surgeRetail.document.Item.Store;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConfidentialApiRepository {

    private final MongoTemplate mongoTemplate;


    public ConfidentialApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public void saveClientSecret(ClientSecret cs) {
        mongoTemplate.save(cs);
    }

    public List<Store> findAllStores() {
        return mongoTemplate.findAll(Store.class);
    }

    public List<Store> getStoresByClientId(String clientId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("clientId").is(clientId).and("active").is(true);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, Store.class);
    }

    public List<Store> getStoreByStoreAdminId(String storeAdminId) {
        return mongoTemplate.find(new Query(Criteria.where("storeAdminId").is(storeAdminId)), Store.class);
    }
}
