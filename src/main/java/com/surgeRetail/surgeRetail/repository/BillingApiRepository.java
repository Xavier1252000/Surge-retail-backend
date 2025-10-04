package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.Invoice;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.InvoiceItem;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class BillingApiRepository {

    private final MongoTemplate mongoTemplate;

    public BillingApiRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Invoice invoiceByInvoiceId(String invoiceId) {
        return mongoTemplate.findById(invoiceId, Invoice.class);
    }

    public List<InvoiceItem> findInvoiceItemByStoreId(String storeId){
        Query query = new Query();
        Criteria criteria = Criteria.where("invoiceId").is(storeId);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, InvoiceItem.class);
    }

    public Document getStoreById(String id) {
        Query query = new Query();
        query.fields().include("storeName");
        query.addCriteria(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, Document.class, "stores");
    }
//    public List<Invoice> findInvoice(){
//        Query query = new Query();
//        List<Invoice> invoices = mongoTemplate.find(query, Invoice.class);
//        invoices.forEach(x->{
//            mongoTemplate.save(x);
//        });
//        return invoices;
//    }


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
