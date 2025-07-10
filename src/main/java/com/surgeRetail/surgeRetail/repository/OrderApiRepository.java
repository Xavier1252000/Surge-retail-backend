package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.*;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
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

    public Invoice getGreatestSerialNoInvoice(String storeId){
        Query query = new Query(Criteria.where("storeId").is(storeId));
        query.with(Sort.by(Sort.Order.desc("serialNo")));
        return mongoTemplate.findOne(query, Invoice.class);
    }

    public Invoice saveInvoice(Invoice invoice) {
        return mongoTemplate.save(invoice);
    }

    public List<InvoiceItem> saveAllInvoiceItem(List<InvoiceItem> invoiceItems) {
        return (List<InvoiceItem>)mongoTemplate.insertAll(invoiceItems);
    }

    public void reduceItemsStock(List<InvoiceItem> invoiceItems) {
        invoiceItems.forEach(x->{
            Query query = new Query(Criteria.where("_id").is(x.getItemId()).and("itemStock").gte(x.getQuantity()));
            Update update = new Update().inc("itemStock", -x.getQuantity());
            mongoTemplate.updateFirst(query, update, Item.class);
        });
    }
}
