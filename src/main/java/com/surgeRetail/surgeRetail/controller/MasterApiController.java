package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.master.DiscountMaster;
import com.surgeRetail.surgeRetail.document.master.TaxMaster;
import com.surgeRetail.surgeRetail.document.master.TimezoneMaster;
import com.surgeRetail.surgeRetail.service.MasterApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/master")
public class MasterApiController {

    private final MasterApiService masterApiService;

    public MasterApiController(MasterApiService masterApiService
    ) {
        this.masterApiService = masterApiService;
    }

    @PostMapping("/add-item-category-master")
    public ResponseEntity<ApiResponseHandler> addItemCategoryMaster(@RequestBody ApiRequestHandler apiRequestHandler) {
        String categoryName = apiRequestHandler.getStringValue("categoryName");
        if (StringUtils.isEmpty(categoryName))
            return new ResponseEntity<>(new ApiResponseHandler("please provide itemCategoryName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String description = apiRequestHandler.getStringValue("description");

        List<String> storeIds = apiRequestHandler.getListValue("storeIds", String.class);
        if (CollectionUtils.isEmpty(storeIds))
            return new ResponseEntity<>(new ApiResponseHandler("please provide storeIds", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String parentCategoryId = apiRequestHandler.getStringValue("parentCategoryId");

        ApiResponseHandler apiResponseHandler = masterApiService.addItemCategory(categoryName, description, parentCategoryId, storeIds);
        if (apiResponseHandler.getStatusCode() != 201)
            return new ResponseEntity<>(apiResponseHandler, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponseHandler, HttpStatus.CREATED);
    }

    @PostMapping("/update-item-category-master")
    public ApiResponseHandler updateItemCategoryMaster(@RequestBody Map<String, Object> requestMap) {

        String categoryId = (String) requestMap.get("categoryId");
        if (StringUtils.isEmpty(categoryId))
            return new ApiResponseHandler("please provide categoryId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String categoryName = (String) requestMap.get("categoryName");
        if (StringUtils.isEmpty(categoryName))
            return new ApiResponseHandler("please provide itemCategoryName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String description = (String) requestMap.get("description");

        String parentCategoryId = (String) requestMap.get("parentCategoryId");

        return masterApiService.updateItemCategoryMaster(categoryId, categoryName, description, parentCategoryId);
    }

    @PostMapping("/delete-item-category")
    public ApiResponseHandler deleteCategory(@RequestBody Map<String, Object> requestMap) {
        String categoryId = (String) requestMap.get("categoryId");
        if (StringUtils.isEmpty(categoryId))
            return new ApiResponseHandler("please provide categoryId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        return masterApiService.deleteItemCategory(categoryId);
    }

    @PostMapping("/get-all-item-category-master")
    public ApiResponseHandler getAllItemCategory(@RequestBody ApiRequestHandler apiRequestHandler) {
        List<String> storeIds = apiRequestHandler.getListValue("storeIds", String.class);
        return masterApiService.getAllItemCategoryMaster(storeIds);
    }


//    <------------------------------------------------------ TAX-MASTER ------------------------------------------------------------->

    @PostMapping("/add-tax-master")
    public ApiResponseHandler addTaxMaster(@RequestBody ApiRequestHandler apiRequestHandler) {

        List<String> storeIds = apiRequestHandler.getListValue("storeIds", String.class);

        if (CollectionUtils.isEmpty(storeIds))
            return new ApiResponseHandler("please provide storeIds", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        String taxType = apiRequestHandler.getStringValue("taxType");
        if (StringUtils.isEmpty(taxType))
            return new ApiResponseHandler("please provide taxType", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String taxCode = apiRequestHandler.getStringValue("taxCode");
        if (StringUtils.isEmpty(taxCode))
            return new ApiResponseHandler("please provide taxCode", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        BigDecimal taxPercentage = apiRequestHandler.getBigDecimalValue("taxPercentage");
        if(taxPercentage == null){
            return new ApiResponseHandler("please provide taxPercentage", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String applicableOn = apiRequestHandler.getStringValue("applicableOn");
        if (StringUtils.isEmpty(applicableOn))
            return new ApiResponseHandler("please provide applicableOn", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if (!applicableOn.equals(TaxMaster.APPLICABLE_ON_ITEM) && !applicableOn.equals(TaxMaster.APPLICABLE_ON_INVOICE)
                && !applicableOn.equals(TaxMaster.APPLICABLE_ON_CATEGORY) && !applicableOn.equals(TaxMaster.APPLICABLE_ON_STORE)){
            return new ApiResponseHandler("please provide applicableOn valid values are "+ TaxMaster.APPLICABLE_ON_ITEM+", "+ TaxMaster.APPLICABLE_ON_INVOICE
                    +", " +TaxMaster.APPLICABLE_ON_CATEGORY+", "+TaxMaster.APPLICABLE_ON_STORE, null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        Boolean inclusion = apiRequestHandler.getBooleanValue("inclusionOnBasePrice");
        if (inclusion == null)
            return new ApiResponseHandler("please provide inclusionOnBasePrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String description = apiRequestHandler.getStringValue("description");

        Set<String> applicableCategories = apiRequestHandler.getSetValue("applicableCategories", String.class);
        if (applicableOn.equals(TaxMaster.APPLICABLE_ON_CATEGORY) && CollectionUtils.isEmpty(applicableCategories))
            return new ApiResponseHandler("please provide applicable categories", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if (!applicableOn.equals(TaxMaster.APPLICABLE_ON_CATEGORY) && !CollectionUtils.isEmpty(applicableCategories))
            return new ApiResponseHandler("Category is needed only when applicableOn is category", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        return masterApiService.addTaxMaster(storeIds, taxType, taxCode, taxPercentage, applicableOn, applicableCategories, inclusion, description);
    }


    @PostMapping("/update-tax-master")
    public ResponseEntity<ApiResponseHandler> update(@RequestBody ApiRequestHandler apiRequestHandler) {

        String taxMasterId = apiRequestHandler.getStringValue("id");
        if(StringUtils.isEmpty(taxMasterId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide id", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        List<String> storeIds = apiRequestHandler.getListValue("storeIds", String.class);

        if (CollectionUtils.isEmpty(storeIds))
            return new ResponseEntity<>(new ApiResponseHandler("please provide storeIds", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        String taxType = apiRequestHandler.getStringValue("taxType");
        if (StringUtils.isEmpty(taxType))
            return new ResponseEntity<>(new ApiResponseHandler("please provide taxType", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String taxCode = apiRequestHandler.getStringValue("taxCode");
        if (StringUtils.isEmpty(taxCode))
            return new ResponseEntity<>(new ApiResponseHandler("please provide taxCode", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        BigDecimal taxPercentage = apiRequestHandler.getBigDecimalValue("taxPercentage");
        if(taxPercentage == null){
            return new ResponseEntity<>(new ApiResponseHandler("please provide taxPercentage", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        String applicableOn = apiRequestHandler.getStringValue("applicableOn");
        if (StringUtils.isEmpty(applicableOn))
            return new ResponseEntity<>(new ApiResponseHandler("please provide applicableOn", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        if (!applicableOn.equals(TaxMaster.APPLICABLE_ON_ITEM) && !applicableOn.equals(TaxMaster.APPLICABLE_ON_INVOICE)
                && !applicableOn.equals(TaxMaster.APPLICABLE_ON_CATEGORY) && !applicableOn.equals(TaxMaster.APPLICABLE_ON_STORE)){
            return new ResponseEntity<>(new ApiResponseHandler("please provide applicableOn valid values are "+ TaxMaster.APPLICABLE_ON_ITEM+", "+ TaxMaster.APPLICABLE_ON_INVOICE
                    +", " +TaxMaster.APPLICABLE_ON_CATEGORY+", "+TaxMaster.APPLICABLE_ON_STORE, null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        Boolean inclusion = apiRequestHandler.getBooleanValue("inclusionOnBasePrice");
        if (inclusion == null)
            return new ResponseEntity<>(new ApiResponseHandler("please provide inclusionOnBasePrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String description = apiRequestHandler.getStringValue("description");

        Set<String> applicableCategories = apiRequestHandler.getSetValue("applicableCategories", String.class);
        if (applicableOn.equals(TaxMaster.APPLICABLE_ON_CATEGORY) && CollectionUtils.isEmpty(applicableCategories))
            return new ResponseEntity<>(new ApiResponseHandler("please provide applicable categories", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        if (!applicableOn.equals(TaxMaster.APPLICABLE_ON_CATEGORY) && !CollectionUtils.isEmpty(applicableCategories))
            return new ResponseEntity<>(new ApiResponseHandler("Category is needed only when applicableOn is category", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        Boolean active = apiRequestHandler.getBooleanValue("active");
        if (active == null)
            return new ResponseEntity<>(new ApiResponseHandler("Please provide active status", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        return masterApiService.updateTaxMaster(taxMasterId, taxType, taxCode, taxPercentage, applicableOn, applicableCategories, inclusion, description, active);
    }


    @PostMapping("/tax-master-by-store-id")
    public ResponseEntity<ApiResponseHandler> taxMasterByStoreId(@RequestBody ApiRequestHandler apiRequestHandler){
        String storeId = apiRequestHandler.getStringValue("storeId");
        if (StringUtils.isEmpty(storeId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide storeId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(masterApiService.getTaxMasterByStoreId(storeId), HttpStatus.OK);
    }

    @PostMapping("/tax-master-by-id")
    public ResponseEntity<ApiResponseHandler> taxMasterById(@RequestBody ApiRequestHandler apiRequestHandler){
        String taxMasterId = apiRequestHandler.getStringValue("taxMasterId");
        if (StringUtils.isEmpty(taxMasterId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide taxMasterId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(masterApiService.getTaxMasterById(taxMasterId), HttpStatus.OK);
    }

    @PostMapping("/delete-tax-master-by-id")
    public ResponseEntity<ApiResponseHandler> deleteTaxMasterById(@RequestBody ApiRequestHandler apiRequestHandler){
        String taxMasterId = apiRequestHandler.getStringValue("id");
        if (StringUtils.isEmpty(taxMasterId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide id", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        return masterApiService.deleteTaxMasterById(taxMasterId);
    }
//    <--------------------------------------------------------Discount Master -------------------------------------------------------->

    @PostMapping("/add-discount-master")
    public ApiResponseHandler addDiscountMaster(@RequestBody Map<String, Object> requestMap){
        String discountName = (String) requestMap.get("discountName");
        if(StringUtils.isEmpty(discountName)){
            return new ApiResponseHandler("please provide discountName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        Object disPercent = requestMap.get("discountPercentage");
        BigDecimal discountPercentage = null;
        if (disPercent instanceof BigDecimal) {
            discountPercentage = (BigDecimal) disPercent;
        }else {
            try {
                discountPercentage = new BigDecimal(String.valueOf(disPercent));
            } catch (NumberFormatException e) {
                return new ApiResponseHandler("please provide valid value in discountPercentage", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
            }
        }

        String applicableOn = (String) requestMap.get("applicableOn");
        if (!applicableOn.equals(DiscountMaster.DISCOUNT_APPLICABLE_ON_ITEM) && !applicableOn.equals(DiscountMaster.DISCOUNT_APPLICABLE_ON_INVOICE))
            return new ApiResponseHandler("applicable on can have "+DiscountMaster.DISCOUNT_APPLICABLE_ON_ITEM + " or "+DiscountMaster.DISCOUNT_APPLICABLE_ON_INVOICE, null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String discountCouponCode = (String) requestMap.get("discountCouponCode");
        return masterApiService.addDiscountMaster(discountName, discountPercentage, discountCouponCode, applicableOn);
    }

    @PostMapping("/get-discount-master")
    public ResponseEntity<ApiResponseHandler> getDiscountMaster(@RequestBody ApiRequestHandler apiRequestHandler){
        List<String> storeIds = apiRequestHandler.getListValue("storeIds", String.class);
        if (CollectionUtils.isEmpty(storeIds))
            return new ResponseEntity<>(new ApiResponseHandler("please provide storeIds", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        return masterApiService.getDiscountMasters(storeIds);
    }


//    <------------------------------------------------UnitMaster------------------------------------------------------->
    @PostMapping("/add-unit-master")
    public ResponseEntity<ApiResponseHandler> addUnit(@RequestBody Map<String, Object> requestMap){
        String unit = (String) requestMap.get("unitFullForm");
        if(StringUtils.isEmpty(unit)){
            return new ResponseEntity<>(new ApiResponseHandler("please provide unit", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        String unitNotation = (String) requestMap.get("unitNotation");
        if(StringUtils.isEmpty(unit)){
            return new ResponseEntity<>(new ApiResponseHandler("please provide unit", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        List<String> storeIds = (List<String>) requestMap.get("storeIds");
        if (CollectionUtils.isEmpty(storeIds))
            return new ResponseEntity<>(new ApiResponseHandler("please provide storeIds", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        ApiResponseHandler apiResponseHandler = masterApiService.addUnit(unit, unitNotation, storeIds);
        if (apiResponseHandler.getStatusCode() != 201)
            return new ResponseEntity<>(apiResponseHandler, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(apiResponseHandler, HttpStatus.CREATED);
    }

    @PostMapping("/get-all-unit-master")
    public ResponseEntity<ApiResponseHandler> getAllUnitMaster(@RequestBody ApiRequestHandler apiRequestHandler){
        List<String> storeIds = apiRequestHandler.getListValue("storeIds", String.class);
        return new ResponseEntity<>(masterApiService.getAllUnitMaster(storeIds), HttpStatus.OK);
    }




//    --------------------------------------------------Country master-------------------------------------------------------------
    @PostMapping("/add-country-master")
    public ResponseEntity<ApiResponseHandler> addCountryMaster(@RequestBody ApiRequestHandler apiRequestHandler){
        String name = apiRequestHandler.getStringValue("name");
        if(StringUtils.isEmpty(name)){
            return new ResponseEntity<>(new ApiResponseHandler("please provide name", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        String currency = apiRequestHandler.getStringValue("currency");
        if(StringUtils.isEmpty(currency)){
            return new ResponseEntity<>(new ApiResponseHandler("please provide country", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        String code = apiRequestHandler.getStringValue("code");
        if(StringUtils.isEmpty(name)){
            return new ResponseEntity<>(new ApiResponseHandler("please provide code", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        String symbol = apiRequestHandler.getStringValue("symbol");
        if(StringUtils.isEmpty(symbol)){
            return new ResponseEntity<>(new ApiResponseHandler("please provide symbol", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        String callingCode = apiRequestHandler.getStringValue("callingCode");
        if(StringUtils.isEmpty(callingCode)){
            return new ResponseEntity<>(new ApiResponseHandler("please provide symbol", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        List<TimezoneMaster> timeZones = apiRequestHandler.getListValue("timeZones", TimezoneMaster.class);


        ApiResponseHandler apiResponseHandler = masterApiService.addCountryMaster(name, currency, code, symbol, callingCode, timeZones);
        return new ResponseEntity<>(apiResponseHandler, HttpStatus.CREATED);
    }


    @GetMapping("/get-all-country-masters")
    public ResponseEntity<ApiResponseHandler> getAllCountryMaster(){
        return new ResponseEntity<>(masterApiService.getAllCountryMaters(), HttpStatus.OK);
    }


//    --------------------------------------Roles--------------------------------------
    @PostMapping("/create-role")
    public ResponseEntity<ApiResponseHandler> createRole(@RequestBody ApiRequestHandler apiRequestHandler){
        String id = apiRequestHandler.getStringValue("id");
        String role = apiRequestHandler.getStringValue("roleName");
        if (StringUtils.isEmpty(role))
            return new ResponseEntity<>(new ApiResponseHandler("please provide roleName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String roleType = apiRequestHandler.getStringValue("roleType");
        if (StringUtils.isEmpty(roleType))
            return new ResponseEntity<>(new ApiResponseHandler("please provide roleType, can be Primary or Custom", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        Set<String> canBeAssignedBy = apiRequestHandler.getSetValue("canBeAssignedBy", String.class);
        if (CollectionUtils.isEmpty(canBeAssignedBy))
            return new ResponseEntity<>(new ApiResponseHandler("please provide canBeAssignedBy", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        ApiResponseHandler response = masterApiService.createRole(id, role, roleType, canBeAssignedBy);
        if (response.getStatusCode() != 201 || response.getStatusCode() != 200)
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("get-roles")
    public ResponseEntity<ApiResponseHandler> getRoles(@RequestBody ApiRequestHandler apiRequestHandler){
        String createdBy = apiRequestHandler.getStringValue("createdBy");      //generally superadmin or client id is reuired
        if (StringUtils.isEmpty(createdBy))
            return new ResponseEntity<>(new ApiResponseHandler("please provide the createdBy", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(masterApiService.findRolesByCreatedBy(createdBy), HttpStatus.CREATED);

    }
}
