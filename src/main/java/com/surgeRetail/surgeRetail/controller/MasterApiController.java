package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.master.ItemsCategoryMaster;
import com.surgeRetail.surgeRetail.service.MasterApiService;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
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
        if (StringUtils.isEmpty(applicableOn))
            return new ApiResponseHandler("please provide applicableOn", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

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
}
