package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.master.ItemsCategoryMaster;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MasterApiRepository {

    private final MongoTemplate mongoTemplate;

    public MasterApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }
    public ItemsCategoryMaster addItemCategory(ItemsCategoryMaster icm) {
        return mongoTemplate.save(icm);
    }

    public ItemsCategoryMaster deleteItemCategory(String categoryId) {
        return mongoTemplate.findAndRemove(new Query(Criteria.where("id").is(categoryId)), ItemsCategoryMaster.class);
    }

    public List<ItemsCategoryMaster> getAllItemCategoryMaster() {
        return mongoTemplate.findAll(ItemsCategoryMaster.class);
    }

    public ItemsCategoryMaster findItemCategoryMasterById(String categoryId) {
        return mongoTemplate.findById(categoryId, ItemsCategoryMaster.class);
    }
}
