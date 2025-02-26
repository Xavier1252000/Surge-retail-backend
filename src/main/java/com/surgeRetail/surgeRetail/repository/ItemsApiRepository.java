package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.document.Item.ItemImageInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemsApiRepository {

    private final MongoTemplate mongoTemplate;

    public ItemsApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public Item saveItem(Item item){
        return mongoTemplate.save(item);
    }


    public List<Item> findAllItems(){
        return mongoTemplate.findAll(Item.class);
    }

    public Item getItemById(String itemId){
        return mongoTemplate.findById(itemId, Item.class);
    }

    public List<Item> getItemByIds(List<String> itemIds) {
        return mongoTemplate.find(new Query(Criteria.where("id").in(itemIds)), Item.class);
    }

    public Item findItemById(String itemId) {
        return mongoTemplate.findById(itemId, Item.class);
    }

    public ItemImageInfo saveItemImageInfo(ItemImageInfo itemImageInfo) {
        return mongoTemplate.save(itemImageInfo);
    }
}
