package com.surgeRetail.surgeRetail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.service.ItemsApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.*;

@RestController
@RequestMapping("/items")
public class ItemsApiController {

    private final ItemsApiService itemsApiService;
    private final ObjectMapper objectMapper;

    public ItemsApiController(ItemsApiService itemsApiService,
                              ObjectMapper objectMapper){
        this.itemsApiService = itemsApiService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/add-items-to-store")
    public ResponseEntity<ApiResponseHandler> addItemsToStore(@RequestBody ApiRequestHandler apiRequestHandler){
        System.out.println(apiRequestHandler);

        //  if user is not a store admin, and client or some other authoritative is trying to add item, we have to manually provide the storeId
        String storeId = apiRequestHandler.getStringValue("storeId");
        if (StringUtils.isEmpty(storeId))
            return new ResponseEntity<>(new ApiResponseHandler("Please provide storeId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String itemName = apiRequestHandler.getStringValue("itemName");
        if (StringUtils.isEmpty(itemName))
            return new ResponseEntity<>(new ApiResponseHandler("please provide itemName",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        BigDecimal costPrice = apiRequestHandler.getBigDecimalValue("costPrice");

        BigDecimal profitToGainInPercentage = apiRequestHandler.getBigDecimalValue("profitToGainInPercentage");

        BigDecimal baseSellingPrice = apiRequestHandler.getBigDecimalValue("baseSellingPrice");

        System.out.println(profitToGainInPercentage +"   "+baseSellingPrice);
        
//        if ((profitToGainInPercentage != null && baseSellingPrice != null) || (profitToGainInPercentage == null && baseSellingPrice == null))      // checking both baseSellingPrice and profitToGainInPercentage should not be present
//            return new ResponseEntity<>(new ApiResponseHandler("please provide any one profitToGainInPercentage or baseSellingPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);


        BigDecimal additionalPrice = apiRequestHandler.getBigDecimalValue("additionalPrice");

        String stockUnit = apiRequestHandler.getStringValue("stockUnit");
        if (StringUtils.isEmpty(stockUnit))
            return new ResponseEntity<>(new ApiResponseHandler("please provide stockUnit",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        Set<String> appTaxes = apiRequestHandler.getSetValue("applicableTaxes", String.class);
        Set<String> applicableTaxes = CollectionUtils.isEmpty(appTaxes)?new HashSet<>(): appTaxes;


        Set<String> dmIds = apiRequestHandler.getSetValue("discountMasterIds", String.class);
        Set<String> discountMasterIds = CollectionUtils.isEmpty(dmIds)?new HashSet<>():dmIds;

        String brand = apiRequestHandler.getStringValue("brand");

        Set<String> catIds = apiRequestHandler.getSetValue("categoryIds", String.class);
        Set<String> categoryIds = CollectionUtils.isEmpty(catIds)? new HashSet<>(): catIds;

        String supplierId = apiRequestHandler.getStringValue("supplierId");

        String description = apiRequestHandler.getStringValue("description");

        List<String> imageInfoIds = apiRequestHandler.getListValue("itemImageInfoIds", String.class);
        List<String> itemImageInfoIds = CollectionUtils.isEmpty(imageInfoIds)?new ArrayList<>():imageInfoIds;

        Float itemStock = apiRequestHandler.getFloatValue("itemStock");
        if (itemStock == null || itemStock < 1)
            return new ResponseEntity<>(new ApiResponseHandler("please provide item stock", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);


        Float stockThreshold = apiRequestHandler.getFloatValue("stockThreshold");


        Set<String> tutLinks = apiRequestHandler.getSetValue("tutorialLinks", String.class);
        Set<String> tutorialLinks = CollectionUtils.isEmpty(tutLinks)?new HashSet<>():tutLinks;

        String barcode = apiRequestHandler.getStringValue("barcode");

        Boolean isReturnable = apiRequestHandler.getBooleanValue("isReturnable");
        if (isReturnable == null)
            isReturnable = false;

        Boolean isWarrantyAvailable = apiRequestHandler.getBooleanValue("isWarrantyAvailable");
        Period period = null;
        if (isWarrantyAvailable) {
            Integer warrantyYears = apiRequestHandler.getIntegerValue("warrantyPeriodYears");

            Integer warrantyMonths = apiRequestHandler.getIntegerValue("warrantyPeriodMonths");

            Integer warrantyDays = apiRequestHandler.getIntegerValue("warrantyPeriodDays");

            period = Period.of(warrantyYears==null?0:warrantyYears, warrantyMonths==null?0:warrantyMonths, warrantyDays==null?0:warrantyDays);
        }


        Instant expiryDate = apiRequestHandler.getInstantValue("expiryDate");

        Item item = new Item();
        item.setStoreId(storeId);
        item.setItemName(itemName);
        item.setCostPrice(costPrice);
        item.setProfitToGainInPercentage(profitToGainInPercentage);
        item.setBaseSellingPrice(baseSellingPrice);
        item.setAdditionalPrice(additionalPrice == null?BigDecimal.ZERO:additionalPrice);
        item.setApplicableTaxes(applicableTaxes);
        item.setDiscountMasterIds(discountMasterIds);
        item.setBrand(brand);
        item.setCategoryIds(categoryIds);
        item.setSupplierId(supplierId);
        item.setDescription(description);
        item.setItemImageInfoIds(itemImageInfoIds);
        item.setItemStock(itemStock);
        item.setStockThreshold(stockThreshold);
        item.setTutorialLinks(tutorialLinks);
        item.setBarcode(barcode);
        item.setStockUnit(stockUnit);
        item.setIsReturnable(isReturnable);
        item.setIsWarrantyAvailable(isWarrantyAvailable);
        item.setWarrantyPeriod(period);
        item.setExpiryDate(expiryDate);

        ApiResponseHandler apiResponseHandler = itemsApiService.addItemToStore(item);
        if (apiResponseHandler.getStatusCode() != 201)
            return new ResponseEntity<>(apiResponseHandler, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponseHandler, HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload-item-images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ApiResponseHandler uploadItemImages(@RequestPart List<MultipartFile> file, @RequestPart String itemId) throws IOException {

        if (StringUtils.isEmpty(itemId))
            return new ApiResponseHandler("please provide itemId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        return itemsApiService.addItemImages(itemId, file);
    }

    @PostMapping("/update-item")
    public ResponseEntity<ApiResponseHandler> updateItem(@RequestBody ApiRequestHandler apiRequestHandler){
        String itemId = apiRequestHandler.getStringValue("itemId");
        System.out.println("-----------------------------------------"+itemId);
        if (StringUtils.isEmpty(itemId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide itemId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        System.out.println(apiRequestHandler);

        //  if user is not a store admin, and client or some other authoritative is trying to add item, we have to manually provide the storeId
        String storeId = apiRequestHandler.getStringValue("storeId");
        if (StringUtils.isEmpty(storeId))
            return new ResponseEntity<>(new ApiResponseHandler("Please provide storeId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String itemName = apiRequestHandler.getStringValue("itemName");
        if (StringUtils.isEmpty(itemName))
            return new ResponseEntity<>(new ApiResponseHandler("please provide itemName",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        BigDecimal costPrice = apiRequestHandler.getBigDecimalValue("costPrice");

        BigDecimal profitToGainInPercentage = apiRequestHandler.getBigDecimalValue("profitToGainInPercentage");

        BigDecimal baseSellingPrice = apiRequestHandler.getBigDecimalValue("baseSellingPrice");

        System.out.println(profitToGainInPercentage +"   "+baseSellingPrice);

//        if ((profitToGainInPercentage != null && baseSellingPrice != null) || (profitToGainInPercentage == null && baseSellingPrice == null))      // checking both baseSellingPrice and profitToGainInPercentage should not be present
//            return new ResponseEntity<>(new ApiResponseHandler("please provide any one profitToGainInPercentage or baseSellingPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        BigDecimal additionalPrice = apiRequestHandler.getBigDecimalValue("additionalPrice");

        String stockUnit = apiRequestHandler.getStringValue("stockUnit");
        if (StringUtils.isEmpty(stockUnit))
            return new ResponseEntity<>(new ApiResponseHandler("please provide stockUnit",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        Set<String> appTaxes = apiRequestHandler.getSetValue("applicableTaxes", String.class);
        Set<String> applicableTaxes = CollectionUtils.isEmpty(appTaxes)?new HashSet<>(): appTaxes;


        Set<String> dmIds = apiRequestHandler.getSetValue("discountMasterIds", String.class);
        Set<String> discountMasterIds = CollectionUtils.isEmpty(dmIds)?new HashSet<>():dmIds;

        String brand = apiRequestHandler.getStringValue("brand");

        Set<String> catIds = apiRequestHandler.getSetValue("categoryIds", String.class);
        Set<String> categoryIds = CollectionUtils.isEmpty(catIds)? new HashSet<>(): catIds;

        String supplierId = apiRequestHandler.getStringValue("supplierId");

        String description = apiRequestHandler.getStringValue("description");

        List<String> imageInfoIds = apiRequestHandler.getListValue("itemImageInfoIds", String.class);
        List<String> itemImageInfoIds = CollectionUtils.isEmpty(imageInfoIds)?new ArrayList<>():imageInfoIds;

        Float itemStock = apiRequestHandler.getFloatValue("itemStock");
        if (itemStock == null || itemStock < 1)
            return new ResponseEntity<>(new ApiResponseHandler("please provide item stock", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);


        Float stockThreshold = apiRequestHandler.getFloatValue("stockThreshold");


        Set<String> tutLinks = apiRequestHandler.getSetValue("tutorialLinks", String.class);
        Set<String> tutorialLinks = CollectionUtils.isEmpty(tutLinks)?new HashSet<>():tutLinks;

        String barcode = apiRequestHandler.getStringValue("barcode");

        Boolean isReturnable = apiRequestHandler.getBooleanValue("isReturnable");
        if (isReturnable == null)
            isReturnable = false;

        Boolean isWarrantyAvailable = apiRequestHandler.getBooleanValue("isWarrantyAvailable");
        Period period = null;
        if (isWarrantyAvailable) {
            Integer warrantyYears = apiRequestHandler.getIntegerValue("warrantyPeriodYears");

            Integer warrantyMonths = apiRequestHandler.getIntegerValue("warrantyPeriodMonths");

            Integer warrantyDays = apiRequestHandler.getIntegerValue("warrantyPeriodDays");

            period = Period.of(warrantyYears==null?0:warrantyYears, warrantyMonths==null?0:warrantyMonths, warrantyDays==null?0:warrantyDays);
        }


        Instant expiryDate = apiRequestHandler.getInstantValue("expiryDate");

        Item item = itemsApiService.itemById(itemId);
        if (item == null)
            return new ResponseEntity<>(new ApiResponseHandler("no item found by provided itemId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        item.setId(itemId);
        item.setStoreId(storeId);
        item.setItemName(itemName);
        item.setCostPrice(costPrice);
        item.setProfitToGainInPercentage(profitToGainInPercentage);
        item.setBaseSellingPrice(baseSellingPrice);
        item.setAdditionalPrice(additionalPrice == null?BigDecimal.ZERO:additionalPrice);
        item.setApplicableTaxes(applicableTaxes);
        item.setDiscountMasterIds(discountMasterIds);
        item.setBrand(brand);
        item.setCategoryIds(categoryIds);
        item.setSupplierId(supplierId);
        item.setDescription(description);
        item.setItemImageInfoIds(itemImageInfoIds);
        item.setItemStock(itemStock);
        item.setStockThreshold(stockThreshold);
        item.setTutorialLinks(tutorialLinks);
        item.setBarcode(barcode);
        item.setStockUnit(stockUnit);
        item.setIsReturnable(isReturnable);
        item.setIsWarrantyAvailable(isWarrantyAvailable);
        item.setWarrantyPeriod(period);
        item.setExpiryDate(expiryDate);

        ApiResponseHandler addItemResponse = itemsApiService.updateItem(item);
        if (addItemResponse.getStatusCode() != 201)
            return new ResponseEntity<>(addItemResponse, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(addItemResponse, HttpStatus.CREATED);
    }

    @PostMapping("/get-item-by-id")
    public ResponseEntity<ApiResponseHandler> getItemById(@RequestBody ApiRequestHandler apiRequestHandler){
        String itemId = apiRequestHandler.getStringValue("itemId");
        if (StringUtils.isEmpty(itemId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide itemId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        return itemsApiService.getItemById(itemId);
    }

    @PostMapping("/find-low-stock-items")
    public ApiResponseHandler findLowStockItems(@RequestBody ApiRequestHandler apiRequestHandler){
        String storeId = apiRequestHandler.getStringValue("storeId");
        return itemsApiService.findLowStockItemsInStore(storeId);
    }

    @PostMapping("get-items-by-store-id")
    public ResponseEntity<ApiResponseHandler> getItemsByStoreId(@RequestBody ApiRequestHandler apiRequestHandler){
        String storeId = apiRequestHandler.getStringValue("storeId");
        if (StringUtils.isEmpty(storeId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide storeId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        Integer index = apiRequestHandler.getIntegerValue("index");
        Integer itemPerIndex = apiRequestHandler.getIntegerValue("itemPerIndex");

        if (index !=null && itemPerIndex !=null){
            if (index < 0 || itemPerIndex < 1)
                return new ResponseEntity<>(new ApiResponseHandler("please provide valid values for index and itemPerIndex", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        return itemsApiService.getItemsByStoreId(index, itemPerIndex, storeId);
    }


    @PostMapping("/get-item-by-name-sku-barcode")
    public ResponseEntity<ApiResponseHandler> getItemByNameSkuOrBarcode(@RequestBody ApiRequestHandler apiRequestHandler){
        System.out.println(apiRequestHandler);
        String storeId = apiRequestHandler.getStringValue("storeId");
        if (StringUtils.isEmpty(storeId))
            ApiResponseHandler.createResponse("please provide storeId",null, ResponseStatusCode.BAD_REQUEST);
        String itemName = apiRequestHandler.getStringValue("itemName");
        Integer skuCode = apiRequestHandler.getIntegerValue("skuCode");
        String barCode = apiRequestHandler.getStringValue("barCode");

        return itemsApiService.getItemByNameSkuOrBarCode(itemName, skuCode, barCode, storeId);
    }
}
