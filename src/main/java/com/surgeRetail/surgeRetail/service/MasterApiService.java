package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.master.*;
import com.surgeRetail.surgeRetail.repository.MasterApiRepository;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
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

    public ApiResponseHandler addItemCategory(String categoryName, String description, String parentCategoryId, List<String> storeIds) {

        ItemsCategoryMaster icm = masterApiRepository.findCategoryByName(categoryName);
       if (icm != null){
           icm.getStoreIds().addAll(new HashSet<>(storeIds));
           icm.onUpdate();
           masterApiRepository.addItemCategory(icm);
       }else {
           icm = new ItemsCategoryMaster();
           icm.setCategoryName(categoryName);
           icm.setDescription(description);
           icm.setParentCategoryId(parentCategoryId);
           icm.setStoreIds(new HashSet<>(storeIds));
           icm.onCreate();
           masterApiRepository.addItemCategory(icm);
       }

        return new ApiResponseHandler("Category added successfully!!", icm, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
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

    public ApiResponseHandler deleteItemCategory(String categoryId, Set<String> storeIds) {
        ItemsCategoryMaster itemsCategoryMaster = masterApiRepository.deleteItemCategory(categoryId, storeIds);
        return new ApiResponseHandler("Item category deleted successfully!!", itemsCategoryMaster, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);

    }

    public ApiResponseHandler getAllItemCategoryMaster(List<String> storeIds) {
        List<ItemsCategoryMaster> icmList = masterApiRepository.getAllItemCategoryMaster(storeIds);
        ArrayNode arrayNode = objectMapper.createArrayNode();
        icmList.forEach(e->{
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", e.getId());
            node.put("categoryName", e.getCategoryName());
            node.put("parentCategoryId", e.getParentCategoryId()!=null?e.getParentCategoryId():null);
            node.put("description", e.getDescription());
            arrayNode.add(node);
        });
        return new ApiResponseHandler("All itemCategories", arrayNode, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }


//    <------------------------------------------------------ TAX-MASTER ------------------------------------------------------------->
    public ApiResponseHandler addTaxMaster(List<String> storeIds, String taxType, String taxCode, BigDecimal taxPercentage, String applicableOn, Set<String> applicableCategories, Boolean inclusion, String description) {

        HashSet<String> storeIdsSet = new HashSet<>(storeIds);
        if (!masterApiRepository.storeIdValidationCheck(storeIdsSet))
            return new ApiResponseHandler("please provide only valid storeIds", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);


        if (masterApiRepository.taxMasterExistByTaxCode(taxCode))
            return new ApiResponseHandler("TaxMaster already exist with provided taxCode", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        TaxMaster taxMaster = new TaxMaster();
        taxMaster.setStoreIds(new HashSet<>(storeIds));
        taxMaster.setTaxCode(taxCode);
        taxMaster.setTaxType(taxType);
        taxMaster.setApplicableOn(applicableOn);
        taxMaster.setTaxPercentage(taxPercentage);
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
        node.set("applicableCategories", objectMapper.valueToTree(tm.getApplicableCategories()));
        node.put("createdOn",String.valueOf(tm.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(tm.getModifiedOn()));
        node.put("createdBy",tm.getCreatedBy());
        node.put("active", tm.getActive());
        return new ApiResponseHandler("taxMaster created successfully", node, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }


    public ResponseEntity<ApiResponseHandler> updateTaxMaster(String taxMasterId, String taxType, String taxCode, BigDecimal taxPercentage, String applicableOn, Set<String> applicableCategories, Boolean inclusion, String description, Boolean active) {
        TaxMaster taxMaster = masterApiRepository.findTaxMasterById(taxMasterId);
        if (taxMaster==null)
            return new ResponseEntity<>(new ApiResponseHandler("taxMaster not exist with providedId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        taxMaster.setTaxCode(taxCode);
        taxMaster.setTaxType(taxType);
        taxMaster.setApplicableOn(applicableOn);
        taxMaster.setTaxPercentage(taxPercentage);
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
        node.set("applicableCategories", objectMapper.valueToTree(tm.getApplicableCategories()));
        node.put("createdOn",String.valueOf(tm.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(tm.getModifiedOn()));
        node.put("createdBy",tm.getCreatedBy());
        node.put("active", tm.getActive());
        return new ResponseEntity<>(new ApiResponseHandler("taxMaster updated successfully", node, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false), HttpStatus.OK);
    }


    public ApiResponseHandler getTaxMasterByStoreId(String storeId) {
        List<TaxMaster> taxMasterByStoreId = masterApiRepository.getTaxMasterByStoreId(storeId);
        return new ApiResponseHandler("Tax masters fetched successfully", taxMasterByStoreId, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }

    public ApiResponseHandler getTaxMasterById(String taxMasterId) {
        TaxMaster taxMasterById = masterApiRepository.getTaxMasterById(taxMasterId);
        return new ApiResponseHandler("Tax master fetched successfully", taxMasterById, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }

    public ResponseEntity<ApiResponseHandler> deleteTaxMasterById(String taxMasterId) {
        masterApiRepository.deleteTaxMasterById(taxMasterId);
        return new ResponseEntity<>(new ApiResponseHandler("tax master deleted successfully", null, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false), HttpStatus.OK);
    }

//    ----------------------------------------------------DISCOUNT MASTER--------------------------------------------------------

    public ResponseEntity<ApiResponseHandler> addDiscountMaster(String discountName, BigDecimal discountPercentage,
                                                String discountCouponCode, String applicableOn, Set<String> storeIds) {
        DiscountMaster discountMaster = new DiscountMaster();
        discountMaster.setDiscountName(discountName);
        discountMaster.setDiscountPercentage(discountPercentage);
        discountMaster.setDiscountCouponCode(discountCouponCode);
        discountMaster.setApplicableOn(applicableOn);
        discountMaster.setStoreIds(storeIds);
        discountMaster.onCreate();;
        DiscountMaster savedDiscountMaster = masterApiRepository.saveDiscountMaster(discountMaster);
        return ApiResponseHandler.createResponse("discount master saved successfully!!!", savedDiscountMaster,
                ResponseStatusCode.CREATED);
    }

    public ResponseEntity<ApiResponseHandler> getDiscountMasters(List<String> storeIds) {
        return new ResponseEntity<>(new ApiResponseHandler("discount master fetched successfully", masterApiRepository.getDiscountMastersByStoreId(storeIds), ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false), HttpStatus.OK);
    }

//    --------------------------------------------------Unit master--------------------------------------------------

    public ApiResponseHandler addUnit(String unit, String unitNotation, List<String> storeIds, String unitMasterId) {
        if (masterApiRepository.unitExistByName(unit, storeIds, unitMasterId))
            return new ApiResponseHandler("unit already exists", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        UnitMaster unitMaster = new UnitMaster();
        unitMaster.setUnit(unit);
        unitMaster.setUnitNotation(unitNotation);
        unitMaster.setStoreIds(new HashSet<>(storeIds));
        if (StringUtils.isNotEmpty(unitMasterId)){
            unitMaster.setId(unitMasterId);
            unitMaster.onUpdate();
        } else {
            unitMaster.onCreate();
        }
        return new ApiResponseHandler("now you can add item with measurement in: "+unit, masterApiRepository.saveUnitMaster(unitMaster), ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler getAllUnitMaster(List<String> storeIds) {
        return new ApiResponseHandler("Units fetched successfully",masterApiRepository.getAllUnitMaster(storeIds), ResponseStatus.SUCCESS, ResponseStatusCode
                .SUCCESS, false);
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

    public ApiResponseHandler getAllCountryMaters() {
        List<CountryMaster> allCountryMasters = masterApiRepository.getAllCountryMasters();
        List<TimezoneMaster> allTimezoneMaster = masterApiRepository.getAllTimezoneMaster();
        ArrayNode root = objectMapper.createArrayNode();

        allCountryMasters.forEach(e->{
            List<TimezoneMaster> timeZones = allTimezoneMaster.stream().filter(x -> x.getCountryId().equals(e.getId())).toList();
            ObjectNode node = objectMapper.valueToTree(e);
            node.set("timeZones", objectMapper.valueToTree(timeZones));
            root.add(node);
        });
        return new ApiResponseHandler("countryMaster fetched successfully", root, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }

    public ApiResponseHandler createRole(String id, String roleName, String roleTypee, Set<String> canBeAssignedBy) {
        Roles role = new Roles();
        if (StringUtils.isEmpty(id))
            role.setId(id);
        role.setRole(roleName);
        role.setCanBeAssignedBy(canBeAssignedBy);
        role.setRoleType(roleTypee);
        role.onCreate();

        return new ApiResponseHandler("role created successfully",masterApiRepository.saveRole(role), ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler findRolesByCreatedBy(String createdBy) {
        return new ApiResponseHandler("success", masterApiRepository.findRolesByCreatedBy(createdBy), ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }

    public ResponseEntity<ApiResponseHandler> getUnitMasterById(String unitMasterId) {
        try {
            return ApiResponseHandler.createResponse("success",masterApiRepository.getUnitMasterById(unitMasterId),
                    ResponseStatusCode.SUCCESS);
        } catch (Exception e) {
            return ApiResponseHandler.createResponse(e.getMessage(), null, ResponseStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ApiResponseHandler> changeUnitStatus(String unitMasterId) {
        return ApiResponseHandler.createResponse("success", masterApiRepository.changeUnitStatus(unitMasterId),
                ResponseStatusCode.SUCCESS);
    }
}
