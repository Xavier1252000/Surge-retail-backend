package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.master.*;
import com.surgeRetail.surgeRetail.document.store.Store;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import io.micrometer.common.util.StringUtils;
import org.springframework.boot.autoconfigure.graphql.data.GraphQlQueryByExampleAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Set;

@Repository
public class MasterApiRepository {

    private final MongoTemplate mongoTemplate;

    public MasterApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }
    public ItemsCategoryMaster addItemCategory(ItemsCategoryMaster icm) {
        return mongoTemplate.save(icm);
    }

    public ItemsCategoryMaster deleteItemCategory(String categoryId, Set<String> storeIds) {
        ItemsCategoryMaster itemCategory = mongoTemplate.findOne(new Query(Criteria.where("id").is(categoryId)), ItemsCategoryMaster.class);
        if (itemCategory.getStoreIds().size() > storeIds.size()){
            itemCategory.getStoreIds().removeAll(storeIds);
        }else {
            mongoTemplate.remove(itemCategory);
        }
        return itemCategory;
    }

    public List<ItemsCategoryMaster> getAllItemCategoryMaster(List<String> storeIds) {
        return mongoTemplate.find(new Query(Criteria.where("storeIds").in(storeIds)), ItemsCategoryMaster.class);
    }

    public ItemsCategoryMaster findItemCategoryMasterById(String categoryId) {
        return mongoTemplate.findById(categoryId, ItemsCategoryMaster.class);
    }

    public TaxMaster saveTaxMaster(TaxMaster taxMaster) {
        return mongoTemplate.save(taxMaster);
    }

    public TaxMaster findTaxMasterById(String taxMasterId) {
        return mongoTemplate.findById(taxMasterId, TaxMaster.class);
    }

    public List<TaxMaster> findTaxMasterByIds(Set<String> applicableTaxMasterIds) {
        return mongoTemplate.find(new Query(Criteria.where("id").in(applicableTaxMasterIds)), TaxMaster.class);
    }

    public List<DiscountMaster> findDiscountMasterByIds(Set<String> discountMasterIds) {
        return mongoTemplate.find(new Query(Criteria.where("id").in(discountMasterIds)), DiscountMaster.class);
    }

    public DiscountMaster saveDiscountMaster(DiscountMaster discountMaster) {
        return mongoTemplate.save(discountMaster);
    }

    public UnitMaster saveUnitMaster(UnitMaster unitMaster) {
        return mongoTemplate.save(unitMaster);
    }

    public List<UnitMaster> getAllUnitMaster(List<String> storeIds) {
        return mongoTemplate.find(new Query(Criteria.where("storeIds").in(storeIds)), UnitMaster.class);
    }
    
    public List<DiscountMaster> findActiveInvoiceDiscounts(String couponCode){
        Query query = new Query();
        Criteria activeCriteria = Criteria.where("active").is(true);
        Criteria applicableOn = Criteria.where("applicableOn").is(DiscountMaster.DISCOUNT_APPLICABLE_ON_INVOICE);
        Criteria couponCriteria = null;
        if (!StringUtils.isEmpty(couponCode))
            couponCriteria = Criteria.where("discountCouponCode").is(couponCode);

        Criteria criteria = new Criteria();
        if (couponCriteria != null) {
            criteria.andOperator(activeCriteria, applicableOn, couponCriteria);
        }else {
            criteria.andOperator(activeCriteria, applicableOn);
        }
        query.addCriteria(criteria);
        return mongoTemplate.find(query, DiscountMaster.class);
    }


    public List<TaxMaster> findActiveInvoiceTaxMaster(){
        Query query = new Query();
        Criteria activeCriteria = Criteria.where("active").is(true);
        Criteria applicableOn = Criteria.where("applicableOn").is(TaxMaster.APPLICABLE_ON_INVOICE);
        Criteria inclusionOnBasePriceCriteria = Criteria.where("inclusionOnBasePrice").is(false);

        Criteria criteria = new Criteria();
        criteria.andOperator(activeCriteria, applicableOn, inclusionOnBasePriceCriteria);

        query.addCriteria(criteria);
        return mongoTemplate.find(query, TaxMaster.class);
    }

    public CountryMaster saveCountryMaster(CountryMaster countryMaster) {
        return mongoTemplate.save(countryMaster);
    }

    public List<CountryMaster> getAllCountryMasters() {
        return mongoTemplate.findAll(CountryMaster.class);
    }

    public TimezoneMaster saveTimezones(TimezoneMaster timezones) {
        return mongoTemplate.save(timezones);
    }

    public List<TimezoneMaster> getAllTimezoneMaster() {
        return mongoTemplate.findAll(TimezoneMaster.class);
    }

    public Roles saveRole(Roles role) {
        return mongoTemplate.save(role);
    }

    public List<Roles> findRolesByCreatedBy(String createdBy) {
        Query query = new Query();
        query.addCriteria(Criteria.where("createdBy").is(createdBy));
        return mongoTemplate.find(query, Roles.class);
    }

    public List<TaxMaster> getTaxMasterByStoreId(String storeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("storeIds").is(storeId));
        return mongoTemplate.find(query, TaxMaster.class);
    }

    public TaxMaster getTaxMasterById(String taxMasterId) {
        return mongoTemplate.findById(taxMasterId, TaxMaster.class);
    }

    public void deleteTaxMasterById(String taxMasterId) {
        mongoTemplate.remove(new Query(Criteria.where("id").is(taxMasterId)), TaxMaster.class);
    }

    public boolean taxMasterExistByTaxCode(String taxCode) {
        return mongoTemplate.exists(new Query(Criteria.where("taxCode").is(taxCode)), TaxMaster.class);
    }

    public List<DiscountMaster> getDiscountMastersByStoreId(List<String> storeIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("storeIds").in(storeIds));
        return mongoTemplate.find(query, DiscountMaster.class);
    }

    public boolean storeIdValidationCheck(HashSet<String> storeIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(storeIds));
        long count = mongoTemplate.count(query, Store.class);
        if (storeIds.size() == count)
            return true;
        return false;
    }

    public boolean unitExistByName(String unit, List<String> storeIds) {
        return mongoTemplate.exists(new Query(Criteria.where("unit").is(unit).and("storeIds").in(storeIds)), UnitMaster.class);
    }

    public ItemsCategoryMaster findCategoryByName(String categoryName) {
        return mongoTemplate.findOne(new Query(Criteria.where("categoryName").is(categoryName)), ItemsCategoryMaster.class);
    }

    public UnitMaster findUnitById(String unitMasterId) {
        return mongoTemplate.findById(unitMasterId, UnitMaster.class);
    }
}
