package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.orderAndInvoice.*;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Queue;

@Repository
public class OrderApiRepository {

    private final MongoTemplate mongoTemplate;
    public OrderApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public Cart findCartByCustomerId(String customerId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("customerId").is(customerId);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, Cart.class);
    }

    public Cart saveCart(Cart cart) {
        return mongoTemplate.save(cart);
    }

    public ShippingAddress getShippingAddressById(String shippingAddressId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("id").is(shippingAddressId);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, ShippingAddress.class);
    }

    public void saveOrder(Order order) {
        mongoTemplate.save(order);
    }

    public InvoiceItem saveInvoiceItem(InvoiceItem invoiceItem){
        return mongoTemplate.save(invoiceItem);
    }

    public Invoice getGreatestSerialNoInvoice(){
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("serialNo")));
        return mongoTemplate.findOne(query, Invoice.class);
    }

    public Invoice saveInvoice(Invoice invoice) {
        return mongoTemplate.save(invoice);
    }
}
