package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.orderAndInvoice.Invoice;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class BillingApiRepository {

    private final MongoTemplate mongoTemplate;

    public BillingApiRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

//    public List<Invoice> findInvoice(){
//        Query query = new Query();
//        List<Invoice> invoices = mongoTemplate.find(query, Invoice.class);
//        invoices.forEach(x->{
//            mongoTemplate.save(x);
//        });
//        return invoices;
//    }
}
