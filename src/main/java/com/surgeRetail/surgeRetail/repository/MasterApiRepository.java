package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.master.DiscountMaster;
import com.surgeRetail.surgeRetail.document.master.ItemsCategoryMaster;
import com.surgeRetail.surgeRetail.document.master.TaxMaster;
import com.surgeRetail.surgeRetail.document.master.UnitMaster;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import io.micrometer.common.util.StringUtils;
import org.springframework.boot.autoconfigure.graphql.data.GraphQlQueryByExampleAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

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

    public ItemsCategoryMaster deleteItemCategory(String categoryId) {
        return mongoTemplate.findAndRemove(new Query(Criteria.where("id").is(categoryId)), ItemsCategoryMaster.class);
    }

    public List<ItemsCategoryMaster> getAllItemCategoryMaster() {
        return mongoTemplate.findAll(ItemsCategoryMaster.class);
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
}
