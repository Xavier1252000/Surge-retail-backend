package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.document.Item.ItemImageInfo;
import com.surgeRetail.surgeRetail.document.store.Store;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<Item> findLowStockItemsInStore(String storeId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        Criteria storeCriteria = Criteria.where("storeId").is(storeId);
        Criteria thresholdStockCriteria = Criteria.where("itemStock").lt("stockThreshold");
        criteria.andOperator(storeCriteria, thresholdStockCriteria);
        return mongoTemplate.find(query.addCriteria(criteria), Item.class);
    }

    public boolean isStoreExistsById(String storeId) {
        return mongoTemplate.exists(new Query(Criteria.where("id").is(storeId)), Store.class);
    }

    public Map<String, Object> getItemsByStoreId(Integer index, Integer itemPerIndex, String storeId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("storeId").is(storeId);
        query.addCriteria(criteria);

        long count = mongoTemplate.count(query, Item.class); //must be calculated before pagination is applied

        if (index != null && itemPerIndex != null)
            query.with(PageRequest.of(index, itemPerIndex));

        Map<String, Object> responseMap = new HashMap<>();
        List<Item> items = mongoTemplate.find(query, Item.class);
        responseMap.put("items", items);
        responseMap.put("count", count);
        return responseMap;
    }

    public Item getItemByStoreAndGreatestSku(String storeId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("storeId").is(storeId);
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Direction.DESC, "skuCode")).limit(1);
        return mongoTemplate.findOne(query, Item.class);
    }

    public boolean itemExistByName(String itemName, String storeId) {
        System.out.println(1);
        return mongoTemplate.exists(new Query(Criteria.where("itemName").is(itemName).and("storeId").is(storeId)), Item.class);
    }

    public boolean itemExistByNameAndNotById(String id, String itemName, String storeId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("itemName").is(itemName).and("storeId").is(storeId).and("id").nin(Arrays.asList(id));
        query.addCriteria(criteria);
        return mongoTemplate.exists(query, Item.class);
    }

    public List<Item> findItemByNameSkuOrBarCode(String itemName, Integer skuCode, String barCode, String storeId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("storeId").is(storeId);
        if (!StringUtils.isEmpty(itemName)) {
            criteria = criteria.and("itemName").regex(itemName, "i");
            query.addCriteria(criteria);
            return mongoTemplate.find(query, Item.class);
        }

        if (skuCode!=null) {
            criteria.and("skuCode").is(skuCode);
            query.addCriteria(criteria);
            return mongoTemplate.find(query, Item.class);
        }

        if (!StringUtils.isEmpty(barCode)) {
            criteria.and("barCode").is(barCode);
            query.addCriteria(criteria);
            return mongoTemplate.find(query, Item.class);
        }
        return mongoTemplate.find(query, Item.class);
    }
}
