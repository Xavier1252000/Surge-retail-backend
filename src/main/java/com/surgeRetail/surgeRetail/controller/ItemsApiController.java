package com.surgeRetail.surgeRetail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import com.surgeRetail.surgeRetail.service.ItemsApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.time.format.DateTimeParseException;
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
    public ApiResponseHandler addItemsToStore(@RequestBody Map<String, Object> requestMap){

        String storeId = null;

        // if item is registered by storeAdmin, no need to manually provide storeId
        UserDetailsImpl principal = (UserDetailsImpl) (SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (principal.getUser().getRoles().contains(User.USER_ROLE_STORE_ADMIN)){
            if (principal.getStores().size()==1)   //storeAdmin must have only one store
                storeId = principal.getStores().get(0).getId();
        }

        //  if user is not a store admin, and client or some other authoritative is trying to add item, we have to manually provide the storeId
        if (StringUtils.isEmpty(storeId))
            storeId = (String) requestMap.get("storeId");

        if (StringUtils.isEmpty(storeId))
            return new ApiResponseHandler("please provide storeId",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String itemName = (String) requestMap.get("itemName");
        if (StringUtils.isEmpty(itemName))
            return new ApiResponseHandler("please provide itemName",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Object cp = requestMap.get("costPrice");
        BigDecimal costPrice;
        if (cp instanceof BigDecimal) {
            costPrice = (BigDecimal) cp;
        }else {
            try {
                costPrice = new BigDecimal(String.valueOf(cp));
            } catch (Exception e) {
                return new ApiResponseHandler("please provide costPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
            }
        }

        Object profitPercentage = requestMap.get("profitToGainInPercentage");
        BigDecimal profitToGainInPercentage = null;
        

        String bsPrice = String.valueOf(requestMap.get("baseSellingPrice"));
        BigDecimal baseSellingPrice=null;
        
        if (!StringUtils.isEmpty(String.valueOf(profitPercentage)) && !StringUtils.isEmpty(bsPrice) || StringUtils.isEmpty(String.valueOf(profitPercentage)) && StringUtils.isEmpty(bsPrice))      // checking both baseSellingPrice and profitToGainInPercentage should not be present
            return new ApiResponseHandler("please provide any one profitToGainInPercentage or baseSellingPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        
        if (!StringUtils.isEmpty(String.valueOf(profitPercentage))){
            try {
                profitToGainInPercentage = new BigDecimal(String.valueOf(profitPercentage));
            }catch (Exception e){
                return new ApiResponseHandler("please provide valid values in profitToGainInPercentage", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
            }
        }

        if (!StringUtils.isEmpty(bsPrice)){
            try {
                baseSellingPrice = new BigDecimal(bsPrice);
            }catch (Exception e){
                return new ApiResponseHandler("please provide valid values in baseSellingPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
            }
        }

        Object addPrice = requestMap.get("additionalPrice");
        BigDecimal additionalPrice;
        try {
            additionalPrice = new BigDecimal(String.valueOf(addPrice));
        }catch (Exception e){
            return new ApiResponseHandler("please provide additionalPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String stockUnit = (String)requestMap.get("stockUnit");
        if (StringUtils.isEmpty(stockUnit))
            return new ApiResponseHandler("please provide stockUnit",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        List<String> appTaxes = (ArrayList<String>) requestMap.get("applicableTaxes");
        Set<String> applicableTaxes = CollectionUtils.isEmpty(appTaxes)?null: new HashSet<>(appTaxes);


        List<String> dmIds = (List<String>) requestMap.get("discountMasterIds");
        Set<String> discountMasterIds = CollectionUtils.isEmpty(dmIds)?null:new HashSet<>(dmIds);

        String brand = (String) requestMap.get("brand");

        List<String> catIds = (List<String>) requestMap.get("categoryIds");
        Set<String> categoryIds = CollectionUtils.isEmpty(catIds)? null: new HashSet<>(catIds);

        String supplierId = (String)requestMap.get("supplierId");

        String description = (String) requestMap.get("description");

        List<String> imageInfoIds = (List<String>)requestMap.get("itemImageInfoIds");
        List<String> itemImageInfoIds = CollectionUtils.isEmpty(imageInfoIds)?null:new ArrayList<>(imageInfoIds);

        Object stock = requestMap.get("itemStock");
        Float itemStock = null;
        try {
            itemStock = Float.parseFloat(String.valueOf(stock));
        }catch (Exception e){
            return new ApiResponseHandler("please provide valid values in itemStock can be integer or decimal", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }


        Object sThreshold = requestMap.get("stockThreshold");
        Float stockThreshold = null;
        try {
            stockThreshold = Float.parseFloat(String.valueOf(sThreshold));
        }catch (Exception e){
            return new ApiResponseHandler("please provide valid values in stockThreshold can be integer or decimal", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }


        List<String> tutLinks = (List<String>) requestMap.get("tutorialLinks");
        Set<String> tutorialLinks = CollectionUtils.isEmpty(tutLinks)?null:new HashSet(tutLinks);

        String barcode = (String) requestMap.get("barcode");

        Boolean isReturnable = (Boolean) requestMap.get("isReturnable");
        Boolean isWarrantyAvailable = (Boolean) requestMap.get("isWarrantyAvailable");

        String wPeriodYears = String.valueOf(requestMap.get("warrantyPeriodYears"));
        Integer warrantyYears = (wPeriodYears == null || wPeriodYears.isEmpty()) ? 0 : Integer.parseInt(wPeriodYears);

        String wPeriodMonths = String.valueOf(requestMap.get("warrantyPeriodMonths"));
        Integer warrantyMonths = (wPeriodMonths == null || wPeriodMonths.isEmpty()) ? 0 : Integer.parseInt(wPeriodMonths);

        String wPeriodDays = String.valueOf(requestMap.get("warrantyPeriodDays"));
        Integer warrantyDays = (wPeriodDays == null || wPeriodDays.isEmpty()) ? 0 : Integer.parseInt(wPeriodDays);

        Period period = Period.of(warrantyYears, warrantyMonths, warrantyDays);


        String expDate = (String)requestMap.get("expiryDate");
        Instant expiryDate = null;
        if (!StringUtils.isEmpty(expDate)) {
            try {
                expiryDate = Instant.parse(expDate);
            } catch (DateTimeParseException e) {
                return new ApiResponseHandler("Invalid expiryDate format. Please use ISO-8601 format (e.g., 2025-12-31T23:59:59Z)",
                        null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
            }
        }

        Item item = new Item();
        item.setStoreId(storeId);
        item.setItemName(itemName);
        item.setCostPrice(costPrice);
        item.setProfitToGainInPercentage(profitToGainInPercentage);
        item.setBaseSellingPrice(baseSellingPrice);
        item.setAdditionalPrice(additionalPrice);
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

        return itemsApiService.addItemToStore(item);
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
        if (StringUtils.isEmpty(itemId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide itemId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        String storeId = null;

        // if item is registered by storeAdmin, no need to manually provide storeId
        UserDetailsImpl principal = (UserDetailsImpl) (SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (principal.getUser().getRoles().contains(User.USER_ROLE_STORE_ADMIN)){
            if (principal.getStores().size()==1)   //storeAdmin must have only one store
                storeId = principal.getStores().get(0).getId();
        }

        //  if user is not a store admin, and client or some other authoritative is trying to add item, we have to manually provide the storeId
        if (StringUtils.isEmpty(storeId))
            storeId = apiRequestHandler.getStringValue("storeId");

        if (StringUtils.isEmpty(storeId))
            return new ResponseEntity<>(new ApiResponseHandler("please provide storeId",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String itemName = apiRequestHandler.getStringValue("itemName");
        if (StringUtils.isEmpty(itemName))
            return new ResponseEntity<>(new ApiResponseHandler("please provide itemName",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        BigDecimal costPrice = apiRequestHandler.getBigDecimalValue("costPrice");
        if(costPrice == null || costPrice.compareTo(BigDecimal.ZERO)<=0)
            return new ResponseEntity<>(new ApiResponseHandler("please provide valid costPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        BigDecimal profitToGainInPercentage = apiRequestHandler.getBigDecimalValue("profitToGainInPercentage");

        BigDecimal baseSellingPrice = apiRequestHandler.getBigDecimalValue("baseSellingPrice");

        if (profitToGainInPercentage != null && baseSellingPrice != null || profitToGainInPercentage == null && baseSellingPrice == null)      // checking both baseSellingPrice and profitToGainInPercentage should not be present
            return new ResponseEntity<>(new ApiResponseHandler("please provide any one profitToGainInPercentage or baseSellingPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        if (profitToGainInPercentage != null && profitToGainInPercentage.compareTo(BigDecimal.ZERO) < 0){
            return new ResponseEntity<>(new ApiResponseHandler("please provide valid values in profitToGainInPercentage", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }

        if (baseSellingPrice != null && baseSellingPrice.compareTo(BigDecimal.ZERO) < 0){
            return new ResponseEntity<>(new ApiResponseHandler("please provide valid values in baseSellingPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);
        }


        BigDecimal additionalPrice = apiRequestHandler.getBigDecimalValue("additionalPrice");
        if (additionalPrice != null && additionalPrice.compareTo(BigDecimal.ZERO) < 0)
            return new ResponseEntity<>(new ApiResponseHandler("additionalPrice can't be less than zero", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        String stockUnit = apiRequestHandler.getStringValue("stockUnit");
        if (StringUtils.isEmpty(stockUnit))
            return new ResponseEntity<>(new ApiResponseHandler("please provide stockUnit",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        Set<String> applicableTaxes = apiRequestHandler.getSetValue("applicableTaxes", String.class);

        Set<String> discountMasterIds = apiRequestHandler.getSetValue("discountMasterIds", String.class);

        String brand = apiRequestHandler.getStringValue("brand");

        Set<String> categoryIds = apiRequestHandler.getSetValue("categoryIds", String.class);

        String supplierId = apiRequestHandler.getStringValue("supplierId");

        String description = apiRequestHandler.getStringValue("description");

        Float itemStock = apiRequestHandler.getFloatValue("itemStock");
        if (itemStock == null || itemStock < 0)
            return new ResponseEntity<>(new ApiResponseHandler("itemStock can't be negative", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        Float stockThreshold =  apiRequestHandler.getFloatValue("stockThreshold");
        if (stockThreshold == null || stockThreshold < 0)
            return new ResponseEntity<>(new ApiResponseHandler("please provide valid values in stockThreshold can be integer or decimal", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

        Set<String> tutorialLinks = apiRequestHandler.getSetValue("tutorialLinks", String.class);

        String barcode = apiRequestHandler.getStringValue("barcode");

        Boolean isReturnable = apiRequestHandler.getBooleanValue("isReturnable");
        Boolean isWarrantyAvailable = apiRequestHandler.getBooleanValue("isWarrantyAvailable");

        Integer warrantyYears = apiRequestHandler.getIntegerValue("warrantyPeriodYears");
        Integer warrantyMonths = apiRequestHandler.getIntegerValue("warrantyPeriodMonths");
        Integer warrantyDays = apiRequestHandler.getIntegerValue("warrantyPeriodDays");
        Period period = Period.of(warrantyYears, warrantyMonths, warrantyDays);

        Instant expiryDate = apiRequestHandler.getInstantValue("expiryDate");

        Item item = new Item();
        item.setId(itemId);
        item.setStoreId(storeId);
        item.setItemName(itemName);
        item.setCostPrice(costPrice);
        item.setProfitToGainInPercentage(profitToGainInPercentage);
        item.setBaseSellingPrice(baseSellingPrice);
        item.setAdditionalPrice(additionalPrice);
        item.setApplicableTaxes(applicableTaxes);
        item.setDiscountMasterIds(discountMasterIds);
        item.setBrand(brand);
        item.setCategoryIds(categoryIds);
        item.setSupplierId(supplierId);
        item.setDescription(description);
        item.setItemStock(itemStock);
        item.setStockThreshold(stockThreshold);
        item.setTutorialLinks(tutorialLinks);
        item.setBarcode(barcode);
        item.setStockUnit(stockUnit);
        item.setIsReturnable(isReturnable);
        item.setIsWarrantyAvailable(isWarrantyAvailable);
        item.setWarrantyPeriod(period);
        item.setExpiryDate(expiryDate);

        ApiResponseHandler addItemResponse = itemsApiService.addItemToStore(item);
        if (addItemResponse.getStatusCode() != 201)
            return new ResponseEntity<>(addItemResponse, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(addItemResponse, HttpStatus.CREATED);
    }

    @PostMapping("/get-item-by-id")
    public ApiResponseHandler getItemById(@RequestBody ApiRequestHandler apiRequestHandler){
        String itemId = apiRequestHandler.getStringValue("itemId");
        if (StringUtils.isEmpty(itemId))
            return new ApiResponseHandler("please provide itemId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

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
}
