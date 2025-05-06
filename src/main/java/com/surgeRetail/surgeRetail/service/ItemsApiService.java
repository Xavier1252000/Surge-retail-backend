package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.document.Item.ItemImageInfo;
import com.surgeRetail.surgeRetail.document.master.DiscountMaster;
import com.surgeRetail.surgeRetail.document.master.TaxMaster;
import com.surgeRetail.surgeRetail.document.store.Store;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.helper.ImageUploadService;
import com.surgeRetail.surgeRetail.repository.ItemsApiRepository;
import com.surgeRetail.surgeRetail.repository.MasterApiRepository;
import com.surgeRetail.surgeRetail.utils.AppUtils;
import com.surgeRetail.surgeRetail.utils.AuthenticatedUserDetails;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemsApiService {
    private final ItemsApiRepository itemsApiRepository;
    private final MasterApiRepository masterApiRepository;
    private final ImageUploadService imageUploadService;
    private final ObjectMapper objectMapper;

    public ItemsApiService(ItemsApiRepository itemsApiRepository,
                           MasterApiRepository masterApiRepository,
                           ImageUploadService imageUploadService,
                           ObjectMapper objectMapper){
        this.itemsApiRepository = itemsApiRepository;
        this.masterApiRepository = masterApiRepository;
        this.imageUploadService = imageUploadService;
        this.objectMapper = objectMapper;
    }
    public ApiResponseHandler addItemToStore(Item item) {

        String storeId = item.getStoreId();
        if(!itemsApiRepository.isStoreExistsById(storeId))
            return new ApiResponseHandler("store not exists with providedId",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

//        Setting profit percentage or profitMargin with the help of other one
        if (item.getProfitToGainInPercentage() != null) {
            BigDecimal profitMargin = item.getProfitToGainInPercentage()
                    .multiply(item.getCostPrice())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP) // Higher precision
                    .setScale(2, RoundingMode.HALF_UP); // Final rounding to 2 decimals

            item.setBaseSellingPrice(item.getCostPrice().add(profitMargin).setScale(2, RoundingMode.HALF_UP));
        } else {
            BigDecimal profitToGainPercentage = item.getBaseSellingPrice()
                    .subtract(item.getCostPrice())
                    .divide(item.getCostPrice(), 4, RoundingMode.HALF_UP) // Higher precision
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP); // Final rounding to 2 decimals

            item.setProfitToGainInPercentage(profitToGainPercentage);
        }

        Set<String> applicableTaxMasterIds = item.getApplicableTaxes();
        if (!CollectionUtils.isEmpty(applicableTaxMasterIds)) {
            List<TaxMaster> taxMasterByIds = masterApiRepository.findTaxMasterByIds(applicableTaxMasterIds);
            Set<String> retrievedTaxMasterIds = taxMasterByIds.stream().map(x -> x.getId()).collect(Collectors.toSet());

            for (String e : applicableTaxMasterIds) {
                if (!retrievedTaxMasterIds.contains(e))
                    return new ApiResponseHandler("tax not found with taxMasterId: " + e, null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
            }


            BigDecimal totalTaxPrice = BigDecimal.valueOf(0);
            for (TaxMaster t : taxMasterByIds) {
                BigDecimal taxPercentage = t.getTaxPercentage();
                BigDecimal taxPrice = (item.getBaseSellingPrice().multiply(taxPercentage)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                totalTaxPrice = totalTaxPrice.add(taxPrice);
            }
            item.setTotalTaxPrice(totalTaxPrice);
        }


//      Setting  Discount master     -------------------->
        Set<String> discountMasterIds = item.getDiscountMasterIds();
        if (!CollectionUtils.isEmpty(discountMasterIds)) {
            List<DiscountMaster> discountMasterByIds = masterApiRepository.findDiscountMasterByIds(discountMasterIds);
            Set<String> retDiscountMasterIds = discountMasterByIds.stream().map(x -> x.getId()).collect(Collectors.toSet());
            for (String e : discountMasterIds) {
                if (!retDiscountMasterIds.contains(e))
                    return new ApiResponseHandler("discount not found by discountMasterId: " + e, null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
            }
            BigDecimal totalDiscountPrice = null;
            for (DiscountMaster d : discountMasterByIds) {
                BigDecimal discountPercentage = d.getDiscountPercentage();
                BigDecimal discountPrice = (item.getBaseSellingPrice().multiply(discountPercentage)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                totalDiscountPrice = item.getTotalDiscountPrice().add(discountPrice);
            }
            item.setTotalDiscountPrice(totalDiscountPrice);
        }

        item.setFinalPrice(item.getBaseSellingPrice()
                .add(item.getTotalTaxPrice())
                .add(item.getAdditionalPrice())
                .subtract(item.getTotalDiscountPrice()!=null?item.getTotalDiscountPrice():BigDecimal.ZERO));

        item.setProfitMargin(item.getFinalPrice().subtract(item.getTotalTaxPrice()).subtract(item.getTotalDiscountPrice()!=null?item.getTotalDiscountPrice():BigDecimal.ZERO).subtract(item.getCostPrice()).setScale(2, RoundingMode.HALF_UP));


        item.setMarkupPercentage(item.getProfitMargin().divide(item.getCostPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        item.onCreate();
        itemsApiRepository.saveItem(item);


        return new ApiResponseHandler("item added successfully to store", item, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler addItemImages(String itemId, List<MultipartFile> images) throws IOException {
        Item item = itemsApiRepository.findItemById(itemId);
        if (item == null)
            return new ApiResponseHandler("item not found by provided id", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        List<Map<String, Object>> response = imageUploadService.uploadMultipartFiles(images);
        List<String> imageInfoIds = new ArrayList<>();
        for (Map<String, Object> m:response){
            ItemImageInfo itemImageInfo = new ItemImageInfo();
            itemImageInfo.setImageUrl((String) m.get("secure_url"));
            itemImageInfo.setItemId(itemId);
            itemImageInfo.setImgUploadResponse(m);
            itemImageInfo.onCreate();
            ItemImageInfo savedImageInfo = itemsApiRepository.saveItemImageInfo(itemImageInfo);
            imageInfoIds.add(savedImageInfo.getId());
        }
        if (CollectionUtils.isEmpty(item.getItemImageInfoIds())){
            item.setItemImageInfoIds(imageInfoIds);
        }else {
            item.getItemImageInfoIds().addAll(imageInfoIds);
        }
        itemsApiRepository.saveItem(item);

        return new ApiResponseHandler("images uploaded successfully", item, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }

    public ApiResponseHandler findLowStockItemsInStore(String storeId) {

        if (AuthenticatedUserDetails.getUserDetails().getAuthorities().contains(User.USER_ROLE_STORE_ADMIN)) {
            Store store = AuthenticatedUserDetails.getUserDetails().getStores().get(0);
            storeId = store.getId();
        }
        if (StringUtils.isEmpty(storeId))
            return new ApiResponseHandler("please provide storeId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        List<Item> lowStockItems = itemsApiRepository.findLowStockItemsInStore(storeId);

        ObjectNode node;
        try {
            node = AppUtils.mapObjectToObjectNode(lowStockItems);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return new ApiResponseHandler("stock low", node, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }

    public ApiResponseHandler getItemById(String itemId) {
        Item itemById = itemsApiRepository.getItemById(itemId);
        return new ApiResponseHandler("Item fetched successfully", itemById, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
    }

    public ResponseEntity<ApiResponseHandler> getItemsByStoreId(Integer index, Integer itemPerIndex, String storeId) {
        Map<String, Object> itemsByStoreId = itemsApiRepository.getItemsByStoreId(index, itemPerIndex, storeId);
        ObjectNode root = objectMapper.createObjectNode();

        ArrayNode itemsArray = objectMapper.createArrayNode();
        Object items = itemsByStoreId.get("items");
        if (items instanceof List<?> itemsList && itemsList.stream().allMatch(item -> item instanceof Item)){
            itemsList.forEach(item -> itemsArray.add(objectMapper.valueToTree(item)));
            root.set("items", itemsArray);
        }
        Integer count = Integer.parseInt(String.valueOf(itemsByStoreId.get("count")));
        root.put("totalItems", count);

        if (index != null && itemPerIndex != null) {
            root.put("totalPages", count % itemPerIndex==0? count/itemPerIndex : count/itemPerIndex+1 );
        }

        return new ResponseEntity<>(new ApiResponseHandler("items fetched successfully", root, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false), HttpStatus.OK);
    }
}