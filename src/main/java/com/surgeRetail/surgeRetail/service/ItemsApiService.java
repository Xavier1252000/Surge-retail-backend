package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.document.Item.ItemImageInfo;
import com.surgeRetail.surgeRetail.document.master.DiscountMaster;
import com.surgeRetail.surgeRetail.document.master.TaxMaster;
import com.surgeRetail.surgeRetail.document.master.UnitMaster;
import com.surgeRetail.surgeRetail.document.store.Store;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.helper.ImageUploadService;
import com.surgeRetail.surgeRetail.repository.ItemsApiRepository;
import com.surgeRetail.surgeRetail.repository.MasterApiRepository;
import com.surgeRetail.surgeRetail.repository.PublicApiRepository;
import com.surgeRetail.surgeRetail.utils.AppUtils;
import com.surgeRetail.surgeRetail.utils.AuthenticatedUserDetails;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;

import io.micrometer.common.util.StringUtils;
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
    private final PublicApiRepository publicApiRepository;
    private final ObjectMapper objectMapper;


    public ItemsApiService(ItemsApiRepository itemsApiRepository,
                           MasterApiRepository masterApiRepository,
                           ImageUploadService imageUploadService,
                           PublicApiRepository publicApiRepository,
                           ObjectMapper objectMapper){
        this.itemsApiRepository = itemsApiRepository;
        this.masterApiRepository = masterApiRepository;
        this.imageUploadService = imageUploadService;
        this.publicApiRepository = publicApiRepository;
        this.objectMapper = objectMapper;
    }
    public ApiResponseHandler addItemToStore(Item item) {

        String storeId = item.getStoreId();
        if(!itemsApiRepository.isStoreExistsById(storeId))
            return new ApiResponseHandler("store not exists with providedId",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        System.out.println(item.getId());
        if (item.getId() == null && itemsApiRepository.itemExistByName(item.getItemName(), storeId))
            return new ApiResponseHandler("Item already exist with itemName "+item.getItemName(), null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if (!StringUtils.isEmpty(item.getId()) && itemsApiRepository.itemExistByNameAndNotById(item.getId(), item.getItemName(), item.getStoreId())){
            return new ApiResponseHandler("Item already exist with itemName "+item.getItemName(), null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }


        Item itemByStoreAndGreatestSku = itemsApiRepository.getItemByStoreAndGreatestSku(storeId);
        if (itemByStoreAndGreatestSku != null && itemByStoreAndGreatestSku.getSkuCode() != null) {
            item.setSkuCode(itemByStoreAndGreatestSku.getSkuCode() + 1);
        }else {
            item.setSkuCode(1);
        }
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

        item.setProfitMargin(item.getFinalPrice().subtract(item.getTotalTaxPrice()!=null?item.getTotalTaxPrice():BigDecimal.ZERO).subtract(item.getCostPrice()).setScale(2, RoundingMode.HALF_UP));


        item.setMarkupPercentage(item.getProfitMargin().divide(item.getCostPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        item.onCreate();
        itemsApiRepository.saveItem(item);


        return new ApiResponseHandler("item added successfully to store", item, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }


    public ApiResponseHandler updateItem(Item item) {

        String storeId = item.getStoreId();
        if(!itemsApiRepository.isStoreExistsById(storeId))
            return new ApiResponseHandler("store not exists with providedId",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        System.out.println(item.getId());
        if (item.getId() == null && itemsApiRepository.itemExistByName(item.getItemName(), storeId))
            return new ApiResponseHandler("Item already exist with itemName "+item.getItemName(), null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if (!StringUtils.isEmpty(item.getId()) && itemsApiRepository.itemExistByNameAndNotById(item.getId(), item.getItemName(), item.getStoreId())){
            return new ApiResponseHandler("Item already exist with itemName "+item.getItemName(), null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

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

        item.setProfitMargin(item.getFinalPrice().subtract(item.getTotalTaxPrice()!=null?item.getTotalTaxPrice():BigDecimal.ZERO).subtract(item.getCostPrice()).setScale(2, RoundingMode.HALF_UP));


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

    public ResponseEntity<ApiResponseHandler> getItemById(String itemId) {
        Item itemById = itemsApiRepository.getItemById(itemId);
        if (itemById == null)
            return new ResponseEntity<>(new ApiResponseHandler("no item found by provided id", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        UnitMaster unitById = masterApiRepository.findUnitById(itemById.getStockUnit());
        User itemCreatedBy = publicApiRepository.findUserByUserId(itemById.getCreatedBy());
        User itemModifiedBy = publicApiRepository.findUserByUserId(itemById.getModifiedBy());
        List<TaxMaster> applicableTaxes = masterApiRepository.findTaxMasterByIds(itemById.getApplicableTaxes());
        List<DiscountMaster> applicableDiscounts = masterApiRepository.findDiscountMasterByIds(itemById.getDiscountMasterIds());


        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", itemById.getId());
        node.put("storeId", itemById.getStoreId());
        node.put("itemName", itemById.getItemName());
        node.put("skuCode", itemById.getSkuCode());
        node.put("costPrice", itemById.getCostPrice());
        node.put("profitToGainInPercentage", itemById.getProfitToGainInPercentage());
        node.put("baseSellingPrice", itemById.getBaseSellingPrice());
        node.put("additionalPrice", itemById.getAdditionalPrice());
        node.put("totalTaxPrice", itemById.getTotalTaxPrice());
        node.put("totalDiscountPrice", itemById.getTotalDiscountPrice());
        node.put("finalPrice", itemById.getFinalPrice());
        node.put("profitMargin", itemById.getProfitMargin());
        node.put("markupPercentage", itemById.getMarkupPercentage());
        node.put("brand", itemById.getBrand());
        node.putPOJO("categoryIds", itemById.getCategoryIds());
        node.put("supplierId", itemById.getSupplierId());
        node.put("description", itemById.getDescription());
        node.putPOJO("itemImageInfoIds", itemById.getItemImageInfoIds());
        node.put("itemStock", itemById.getItemStock());
        node.put("stockThreshold", itemById.getStockThreshold());
        node.putPOJO("tutorialLinks", itemById.getTutorialLinks());
        node.put("barcode", itemById.getBarcode());
        node.put("stockUnit", unitById == null ? "-" : unitById.getUnit() + " (" + unitById.getUnitNotation() + ")");
        node.put("stockUnitId", itemById.getStockUnit());

        node.put("thresholdQuantityForAddTax", itemById.getThresholdQuantityForAddTax());
        node.put("isReturnable", itemById.getIsReturnable());
        node.put("isWarrantyAvailable", itemById.getIsWarrantyAvailable());

        if (Objects.nonNull(itemById.getWarrantyPeriod())) {
            int years = itemById.getWarrantyPeriod().getYears();
            int months = itemById.getWarrantyPeriod().getMonths();
            int days = itemById.getWarrantyPeriod().getDays();
            node.put("warrantyPeriod", (years != 0?years+ " Years ": "") +" "+ (months !=0?months+ " months":"") + " " + (days != 0?days+" days":""));
            if (years != 0)
                node.put("warrantyYears", years);

            if (months != 0)
                node.put("warrantyMonths", months);

            if(days != 0)
                node.put("warrantyDays", days);
        }
        node.put("expiryDate", itemById.getExpiryDate() != null ? itemById.getExpiryDate().toString() : null);
        node.putPOJO("applicableTaxes", objectMapper.valueToTree(applicableTaxes));
        node.putPOJO("applicableDiscounts", objectMapper.valueToTree(applicableDiscounts));
        node.put("createdBy", itemCreatedBy.getFirstName()+" "+itemCreatedBy.getLastName());
        node.put("createdById", itemCreatedBy.getId());
        node.put("modifiedBy", itemModifiedBy.getFirstName()+" "+itemModifiedBy.getLastName());
        node.put("modifiedById", itemModifiedBy.getId());


        return new ResponseEntity<>(new ApiResponseHandler("Item fetched successfully", node, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponseHandler> getItemsByStoreId(Integer index, Integer itemPerIndex, String storeId) {
        Map<String, Object> itemsByStoreId = itemsApiRepository.getItemsByStoreId(index, itemPerIndex, storeId);
        ObjectNode root = objectMapper.createObjectNode();

        List<UnitMaster> units = masterApiRepository.getAllUnitMaster(Collections.singletonList(storeId));

        ArrayNode itemsArray = objectMapper.createArrayNode();
        List<Item> items = (List<Item>)itemsByStoreId.get("items");

        items.forEach(e -> {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", e.getId());
            node.put("storeId", e.getStoreId());
            node.put("itemName", e.getItemName());
            node.put("skuCode", e.getSkuCode());
            node.put("costPrice", e.getCostPrice());
            node.put("profitToGainInPercentage", e.getProfitToGainInPercentage());
            node.put("baseSellingPrice", e.getBaseSellingPrice());
            node.put("additionalPrice", e.getAdditionalPrice());
            node.put("totalTaxPrice", e.getTotalTaxPrice());
            node.put("totalDiscountPrice", e.getTotalDiscountPrice());
            node.put("finalPrice", e.getFinalPrice());
            node.put("profitMargin", e.getProfitMargin());
            node.put("markupPercentage", e.getMarkupPercentage());
            node.put("brand", e.getBrand());
            node.putPOJO("categoryIds", e.getCategoryIds());
            node.put("supplierId", e.getSupplierId());
            node.put("description", e.getDescription());
            node.putPOJO("itemImageInfoIds", e.getItemImageInfoIds());
            node.put("itemStock", e.getItemStock());
            node.put("stockThreshold", e.getStockThreshold());
            node.putPOJO("tutorialLinks", e.getTutorialLinks());
            node.put("barcode", e.getBarcode());

            UnitMaster unitMaster = units.stream()
                    .filter(x -> x.getId().equals(e.getStockUnit()))
                    .findFirst()
                    .orElse(null);
            node.put("stockUnit", unitMaster == null ? "-" : unitMaster.getUnit() + " (" + unitMaster.getUnitNotation() + ")");
            node.put("stockUnitId", e.getStockUnit());

            node.put("thresholdQuantityForAddTax", e.getThresholdQuantityForAddTax());
            node.put("isReturnable", e.getIsReturnable());
            node.put("isWarrantyAvailable", e.getIsWarrantyAvailable());

            if (Objects.nonNull(e.getWarrantyPeriod())) {
                int years = e.getWarrantyPeriod().getYears();
                int months = e.getWarrantyPeriod().getMonths();
                int days = e.getWarrantyPeriod().getDays();
                node.put("warrantyPeriod", (years != 0?years+ " Years ": "") +" "+ (months !=0?months+ " months":"") + " " + (days != 0?days+" days":""));
            }
            node.put("expiryDate", e.getExpiryDate() != null ? e.getExpiryDate().toString() : null);
            node.putPOJO("applicableTaxes", e.getApplicableTaxes());
            node.putPOJO("discountMasterIds", e.getDiscountMasterIds());
            itemsArray.add(node);
        });

        Integer count = Integer.parseInt(String.valueOf(itemsByStoreId.get("count")));
        root.set("items", itemsArray);
        root.put("totalItems", count);

        if (index != null && itemPerIndex != null) {
            root.put("totalPages", count % itemPerIndex==0? count/itemPerIndex : count/itemPerIndex+1 );
        }

        return new ResponseEntity<>(new ApiResponseHandler("items fetched successfully", root, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false), HttpStatus.OK);
    }

    public Item itemById(String itemId) {
        return itemsApiRepository.getItemById(itemId);
    }

    public ResponseEntity<ApiResponseHandler> getItemByNameSkuOrBarCode(String itemName, Integer skuCode, String barCode, String storeId) {
        List<Item> items = itemsApiRepository.findItemByNameSkuOrBarCode(itemName, skuCode, barCode, storeId);
        System.out.println(items);
        return ApiResponseHandler.createResponse("Success", items, ResponseStatusCode.SUCCESS);
    }
}