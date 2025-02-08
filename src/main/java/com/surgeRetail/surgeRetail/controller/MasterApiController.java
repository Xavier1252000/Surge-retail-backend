package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.master.ItemsCategoryMaster;
import com.surgeRetail.surgeRetail.service.MasterApiService;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/master")
public class MasterApiController {

    private final MasterApiService masterApiService;

    public MasterApiController(MasterApiService masterApiService
    ){
        this.masterApiService = masterApiService;
    }

    @PostMapping("/add-item-category-master")
    public ApiResponseHandler addItemCategoryMaster(@RequestBody Map<String, Object> requestMap){
        String categoryName = (String) requestMap.get("categoryName");
        if (StringUtils.isEmpty(categoryName))
            return new ApiResponseHandler("please provide itemCategoryName", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String description = (String) requestMap.get("description");

        String parentCategoryId = (String) requestMap.get("parentCategoryId");

        return masterApiService.addItemCategory(categoryName,description, parentCategoryId);
    }

    @PostMapping("/update-item-category-master")
    public ApiResponseHandler updateItemCategoryMaster(@RequestBody Map<String, Object> requestMap){

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
    public ApiResponseHandler deleteCategory(@RequestBody Map<String, Object> requestMap){
        String categoryId = (String) requestMap.get("categoryId");
        if (StringUtils.isEmpty(categoryId))
            return new ApiResponseHandler("please provide categoryId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        return masterApiService.deleteItemCategory(categoryId);
    }

    @GetMapping("/get-all-item-category-master")
    public ApiResponseHandler getAllItemCategory(){
        return masterApiService.getAllItemCategoryMaster();
    }
}
