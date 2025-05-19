package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.store.Store;
import com.surgeRetail.surgeRetail.document.userAndRoles.ClientDetails;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientDeskApiRepository {

    private final MongoTemplate mongoTemplate;

    public ClientDeskApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public Store saveStore(Store store) {
        return mongoTemplate.save(store);
    }

    public List<Store> findStoreByStaffId(String storeAdminId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("staffIds").is(storeAdminId));
        return mongoTemplate.find(query, Store.class);
    }

    public ClientDetails findClientByClientId(String clientId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(clientId));
        return mongoTemplate.findOne(query, ClientDetails.class);
    }
}
