package com.surgeRetail.surgeRetail.controller;

import com.cloudinary.Api;
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
    public ApiResponseHandler addItemCategoryMaster(@RequestBody Map<String, Object> requestMap) {
        String categoryName = (String) requestMap.get("categoryName");
        if (StringUtils.isEmpty(categoryName))
            return new ApiResponseHandler("please provide itemCategoryName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String description = (String) requestMap.get("description");

        String parentCategoryId = (String) requestMap.get("parentCategoryId");

        return masterApiService.addItemCategory(categoryName, description, parentCategoryId);
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

    @GetMapping("/get-all-item-category-master")
    public ApiResponseHandler getAllItemCategory() {
        return masterApiService.getAllItemCategoryMaster();
    }


//    <------------------------------------------------------ TAX-MASTER ------------------------------------------------------------->

    @PostMapping("/add-tax-master")
    public ApiResponseHandler addTaxMaster(@RequestBody Map<String, Object> requestMap) {
        String taxType = (String) requestMap.get("taxType");
        if (StringUtils.isEmpty(taxType))
            return new ApiResponseHandler("please provide taxType", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String taxCode = (String) requestMap.get("taxCode");
        if (StringUtils.isEmpty(taxCode))
            return new ApiResponseHandler("please provide taxCode", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String taxPercentageString = (String) requestMap.get("taxPercentage");
        BigDecimal taxPercentage;
        try {
            taxPercentage = new BigDecimal(taxPercentageString);
        } catch (Exception e) {
            return new ApiResponseHandler("please provide taxPercentage", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String applicableOn = (String) requestMap.get("applicableOn");
        if (StringUtils.isEmpty(applicableOn))
            return new ApiResponseHandler("please provide applicableOn", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Boolean inclusion = (Boolean) requestMap.get("inclusionOnBasePrice");
        if (StringUtils.isEmpty(applicableOn))
            return new ApiResponseHandler("please provide inclusionOnBasePrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String description = (String) requestMap.get("description");
        List<String> stateIds = (List<String>) requestMap.get("applicableStateIds");
        Set<String> applicableStateIds = new HashSet<>(stateIds);

        List<String> categoryIds = (List<String>) requestMap.get("applicableCategories");
        Set<String> applicableCategories = new HashSet<>(categoryIds);

        return masterApiService.addTaxMaster(taxType, taxCode, taxPercentage, applicableOn, applicableStateIds, applicableCategories, inclusion, description);
    }


    @PostMapping("/update-tax-master")
    public ApiResponseHandler update(@RequestBody Map<String, Object> requestMap) {

        String id = (String) requestMap.get("id");
        if (StringUtils.isEmpty(id))
            return new ApiResponseHandler("please provide id", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String taxType = (String) requestMap.get("taxType");
        if (StringUtils.isEmpty(taxType))
            return new ApiResponseHandler("please provide taxType", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String taxCode = (String) requestMap.get("taxCode");
        if (StringUtils.isEmpty(taxCode))
            return new ApiResponseHandler("please provide taxCode", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String taxPercentageString = (String) requestMap.get("taxPercentage");
        BigDecimal taxPercentage;
        try {
            taxPercentage = new BigDecimal(taxPercentageString);
        } catch (Exception e) {
            return new ApiResponseHandler("please provide taxPercentage", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String applicableOn = (String) requestMap.get("applicableOn");
        if (StringUtils.isEmpty(applicableOn) || (!applicableOn.equals(TaxMaster.APPLICABLE_ON_ITEM) && !applicableOn.equals(TaxMaster.APPLICABLE_ON_INVOICE)))
            return new ApiResponseHandler("please select applicable on, "+TaxMaster.APPLICABLE_ON_ITEM + " or " + TaxMaster.APPLICABLE_ON_INVOICE , null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Boolean inclusion = (Boolean) requestMap.get("inclusionOnBasePrice");
        if (StringUtils.isEmpty(applicableOn))
            return new ApiResponseHandler("please provide inclusionOnBasePrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Boolean active = (Boolean) requestMap.get("active");

        String description = (String) requestMap.get("description");
        List<String> stateIds = (List<String>) requestMap.get("applicableStateIds");
        Set<String> applicableStateIds = new HashSet<>(stateIds);

        List<String> categoryIds = (List<String>) requestMap.get("applicableCategories");
        Set<String> applicableCategories = new HashSet<>(categoryIds);

        return masterApiService.updateTaxMaster(id, taxType, taxCode, taxPercentage, applicableOn, applicableStateIds, applicableCategories, inclusion, description, active);
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


//    <------------------------------------------------UnitMaster------------------------------------------------------->
    @PostMapping("/add-unit-master")
    public ApiResponseHandler addUnit(@RequestBody Map<String, Object> requestMap){
        String unit = (String) requestMap.get("unitFullForm");
        if(StringUtils.isEmpty(unit)){
            return new ApiResponseHandler("please provide unit", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String unitNotation = (String) requestMap.get("unitNotation");
        if(StringUtils.isEmpty(unit)){
            return new ApiResponseHandler("please provide unit", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }
        return masterApiService.addUnit(unit, unitNotation);
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
        String role = apiRequestHandler.getStringValue("roleName");
        if (StringUtils.isEmpty(role))
            return new ResponseEntity<>(new ApiResponseHandler("please provide roleName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String roleTypee = apiRequestHandler.getStringValue("roleType");
        if (StringUtils.isEmpty(roleTypee))
            return new ResponseEntity<>(new ApiResponseHandler("please provide roleType, can be Primary or Custom", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(masterApiService.createRole(role, roleTypee), HttpStatus.CREATED);
    }

    @PostMapping("get-roles")
    public ResponseEntity<ApiResponseHandler> getRoles(@RequestBody ApiRequestHandler apiRequestHandler){
        String createdBy = apiRequestHandler.getStringValue("createdBy");      //generally superadmin or client id is reuired
        if (StringUtils.isEmpty(createdBy))
            return new ResponseEntity<>(new ApiResponseHandler("please provide the createdBy", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(masterApiService.findRolesByCreatedBy(createdBy), HttpStatus.CREATED);

    }
}
