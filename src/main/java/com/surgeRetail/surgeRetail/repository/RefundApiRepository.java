package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.ReturnAndRefund.ReturnRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefundApiRepository {

    private final MongoTemplate mongoTemplate;
    public RefundApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }
    public void createReturnRequest(ReturnRequest returnRequest) {
        mongoTemplate.save(returnRequest);
    }
}
