package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.master.ItemsCategoryMaster;
import com.surgeRetail.surgeRetail.repository.MasterApiRepository;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.stereotype.Service;

import java.util.List;

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
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", e.getId());
            node.put("categoryName", e.getCategoryName());
            node.put("parentCategoryId", e.getParentCategoryId()!=null?e.getParentCategoryId():null);
            node.put("description", e.getDescription());
            arrayNode.add(node);
        });
        return new ApiResponseHandler("All itemCategories", arrayNode, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }
}
