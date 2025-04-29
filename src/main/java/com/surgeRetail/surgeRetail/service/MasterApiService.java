package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.master.*;
import com.surgeRetail.surgeRetail.repository.MasterApiRepository;
import com.surgeRetail.surgeRetail.utils.AppUtils;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class MasterApiService {

    private final MasterApiRepository masterApiRepository;
    private final ObjectMapper objectMapper;

    public MasterApiService(MasterApiRepository masterApiRepository,
                            ObjectMapper objectMapper){
        this.masterApiRepository = masterApiRepository;
        this.objectMapper = objectMapper;
    }

    public ApiResponseHandler addItemCategory(String categoryName, String description, String parentCategoryId) {
        ItemsCategoryMaster icm = new ItemsCategoryMaster();
        icm.setCategoryName(categoryName);
        icm.setDescription(description);
        icm.setParentCategoryId(parentCategoryId);
        icm.onCreate();

        ItemsCategoryMaster itemsCategoryMaster = masterApiRepository.addItemCategory(icm);
        return new ApiResponseHandler("Category added successfully!!", itemsCategoryMaster, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler updateItemCategoryMaster(String categoryId, String categoryName, String description, String parentCategoryId) {
        ItemsCategoryMaster icm = masterApiRepository.findItemCategoryMasterById(categoryId);
        if (icm==null)
            return new ApiResponseHandler("no itemCategory found with provided id", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        icm.setCategoryName(categoryName);
        icm.setDescription(description);
        icm.setParentCategoryId(parentCategoryId);
        icm.onUpdate();

        ItemsCategoryMaster itemsCategoryMaster = masterApiRepository.addItemCategory(icm);
        return new ApiResponseHandler("ItemCategory updated successfully!!", itemsCategoryMaster, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);

    }

    public ApiResponseHandler deleteItemCategory(String categoryId) {
        ItemsCategoryMaster itemsCategoryMaster = masterApiRepository.deleteItemCategory(categoryId);
        return new ApiResponseHandler("Item category deleted successfully!!", itemsCategoryMaster, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);

    }

    public ApiResponseHandler getAllItemCategoryMaster() {
        List<ItemsCategoryMaster> icmList = masterApiRepository.getAllItemCategoryMaster();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        icmList.forEach(e->{
            try {
                arrayNode.add(AppUtils.mapObjectToObjectNode(e));
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }

//            ObjectNode node = objectMapper.createObjectNode();
//            node.put("id", e.getId());
//            node.put("categoryName", e.getCategoryName());
//            node.put("parentCategoryId", e.getParentCategoryId()!=null?e.getParentCategoryId():null);
//            node.put("description", e.getDescription());
//            arrayNode.add(node);
        });
        return new ApiResponseHandler("All itemCategories", arrayNode, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }


//    <------------------------------------------------------ TAX-MASTER ------------------------------------------------------------->
    public ApiResponseHandler addTaxMaster(String taxType, String taxCode, BigDecimal taxPercentage, String applicableOn, Set<String> applicableStateIds, Set<String> applicableCategories, Boolean inclusion, String description) {
        TaxMaster taxMaster = new TaxMaster();
        taxMaster.setTaxCode(taxCode);
        taxMaster.setTaxType(taxType);
        taxMaster.setApplicableOn(applicableOn);
        taxMaster.setTaxPercentage(taxPercentage);
        taxMaster.setApplicableStateIds(applicableStateIds);
        taxMaster.setApplicableCategories(applicableCategories);
        taxMaster.setInclusionOnBasePrice(inclusion);
        taxMaster.setDescription(description);
        taxMaster.onCreate();

        TaxMaster tm = masterApiRepository.saveTaxMaster(taxMaster);
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", tm.getId());
        node.put("taxCode", tm.getTaxCode());
        node.put("taxType",tm.getTaxType());
        node.put("applicableOn",tm.getApplicableOn());
        node.put("taxPercentage",tm.getTaxPercentage());
        node.put("description", description);
        node.set("applicableStateIds",objectMapper.valueToTree(tm.getApplicableStateIds()));
        node.set("applicableCategories", objectMapper.valueToTree(tm.getApplicableCategories()));
        node.put("createdOn",String.valueOf(tm.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(tm.getModifiedOn()));
        node.put("createdBy",tm.getCreatedBy());
        node.put("active", tm.getActive());
        return new ApiResponseHandler("taxMaster created successfully", node, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }


    public ApiResponseHandler updateTaxMaster(String taxMasterId, String taxType, String taxCode, BigDecimal taxPercentage, String applicableOn, Set<String> applicableStateIds, Set<String> applicableCategories, Boolean inclusion, String description, Boolean active) {
        TaxMaster taxMaster = masterApiRepository.findTaxMasterById(taxMasterId);
        if (taxMaster==null)
            return new ApiResponseHandler("taxMaster not exist with providedId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        taxMaster.setTaxCode(taxCode);
        taxMaster.setTaxType(taxType);
        taxMaster.setApplicableOn(applicableOn);
        taxMaster.setTaxPercentage(taxPercentage);
        taxMaster.setApplicableStateIds(applicableStateIds);
        taxMaster.setApplicableCategories(applicableCategories);
        taxMaster.setInclusionOnBasePrice(inclusion);
        taxMaster.setDescription(description);
        taxMaster.setActive(active);
        taxMaster.onUpdate();

        TaxMaster tm = masterApiRepository.saveTaxMaster(taxMaster);
        ObjectNode node = objectMapper.createObjectNode();

        node.put("taxCode", tm.getTaxCode());
        node.put("taxType",tm.getTaxType());
        node.put("applicableOn",tm.getApplicableOn());
        node.put("taxPercentage",tm.getTaxPercentage());
        node.put("description", description);
        node.set("applicableStateIds",objectMapper.valueToTree(tm.getApplicableStateIds()));
        node.set("applicableCategories", objectMapper.valueToTree(tm.getApplicableCategories()));
        node.put("createdOn",String.valueOf(tm.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(tm.getModifiedOn()));
        node.put("createdBy",tm.getCreatedBy());
        node.put("active", tm.getActive());
        return new ApiResponseHandler("taxMaster updated successfully", node, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }

    public ApiResponseHandler addDiscountMaster(String discountName, BigDecimal discountPercentage, String discountCouponCode, String applicableOn) {
        DiscountMaster discountMaster = new DiscountMaster();
        discountMaster.setDiscountName(discountName);
        discountMaster.setDiscountPercentage(discountPercentage);
        discountMaster.setDiscountCouponCode(discountCouponCode);
        discountMaster.setApplicableOn(applicableOn);
        discountMaster.onCreate();;
        DiscountMaster savedDiscountMaster = masterApiRepository.saveDiscountMaster(discountMaster);
        return new ApiResponseHandler("discount master saved successfully!!!", savedDiscountMaster, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
    }

    public ApiResponseHandler addUnit(String unit, String unitNotation) {
        UnitMaster unitMaster = new UnitMaster();
        unitMaster.setUnit(unit);
        unitMaster.setUnitNotation(unitNotation);
        return new ApiResponseHandler("now you can add item with measurement in: "+unit, masterApiRepository.saveUnitMaster(unitMaster), ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    @Transactional
    public ApiResponseHandler addCountryMaster(String name, String currency, String code, String symbol, String callingCode, List<TimezoneMaster> timezones) {

        CountryMaster countryMaster = new CountryMaster(name, currency, code, symbol, callingCode );
        CountryMaster savedCountryMaster = masterApiRepository.saveCountryMaster(countryMaster);
        ArrayNode arrayNode = objectMapper.createArrayNode();
        timezones.forEach(e->{
            e.setCountryId(savedCountryMaster.getId());
            TimezoneMaster savedTZM = masterApiRepository.saveTimezones(e);
            arrayNode.add(objectMapper.valueToTree(savedTZM));
        });
        ObjectNode rootNode = objectMapper.valueToTree(savedCountryMaster);
        rootNode.set("timeZones", arrayNode);

        return new ApiResponseHandler("successfull", rootNode, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }

    public ApiResponseHandler addTimeZoneMaster(String name, String offSet, String countryId, String dtsSupported) {
        return null;
    }

    public ApiResponseHandler getAllCountryMaters() {
        List<CountryMaster> allCountryMasters = masterApiRepository.getAllCountryMasters();
        List<String> countryMasterIds = allCountryMasters.stream().map(CountryMaster::getId).toList();
        List<TimezoneMaster> allTimezoneMaster = masterApiRepository.getAllTimezoneMaster();
        ArrayNode root = objectMapper.createArrayNode();

        allCountryMasters.forEach(e->{
            ArrayNode timeZone = objectMapper.createArrayNode();
            List<TimezoneMaster> timeZones = allTimezoneMaster.stream().filter(x -> x.getCountryId().equals(e.getId())).toList();
            ObjectNode node = objectMapper.valueToTree(e);
            node.set("timeZones", objectMapper.valueToTree(timeZones));
            root.add(node);
        });
        return new ApiResponseHandler("countryMaster fetched successfully", root, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }

    public ApiResponseHandler createRole(String roleName, String roleTypee) {
        Roles role = new Roles();
        role.setRole(roleName);
        role.setRoleType(roleTypee);
        role.onCreate();

        return new ApiResponseHandler("role created successfully",masterApiRepository.saveRole(role), ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler findRolesByCreatedBy(String createdBy) {
        return new ApiResponseHandler("success", masterApiRepository.findRolesByCreatedBy(createdBy), ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }
}
