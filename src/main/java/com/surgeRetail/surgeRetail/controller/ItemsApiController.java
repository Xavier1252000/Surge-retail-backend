package com.surgeRetail.surgeRetail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import com.surgeRetail.surgeRetail.service.ItemsApiService;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.Multipart;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        String profitPercentage = (String) requestMap.get("profitToGainInPercentage");
        BigDecimal profitToGainInPercentage = null;
        

        String bsPrice = (String) requestMap.get("baseSellingPrice");
        BigDecimal baseSellingPrice=null;
        
        if (!StringUtils.isEmpty(profitPercentage) && !StringUtils.isEmpty(bsPrice) || StringUtils.isEmpty(profitPercentage) && StringUtils.isEmpty(bsPrice))      // checking both baseSellingPrice and profitToGainInPercentage should not be present
            return new ApiResponseHandler("please provide any one profitToGainInPercentage or baseSellingPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        
        if (!StringUtils.isEmpty(profitPercentage)){
            try {
                profitToGainInPercentage = new BigDecimal(profitPercentage);
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

        String addPrice = (String)requestMap.get("additionalPrice");
        BigDecimal additionalPrice;
        try {
            additionalPrice = new BigDecimal(addPrice);
        }catch (Exception e){
            return new ApiResponseHandler("please provide additionalPrice", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

        String stockUnit = (String)requestMap.get("stockUnit");
        if (StringUtils.isEmpty(stockUnit))
            return new ApiResponseHandler("please provide stockUnit",null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        List<String> appTaxes = (List<String>) requestMap.get("applicableTaxes");
        Set<String> applicableTaxes = new HashSet<>(appTaxes);

        List<String> dmIds = (List<String>) requestMap.get("discountMasterIds");
        Set<String> discountMasterIds = new HashSet<>(dmIds);

        String brand = (String) requestMap.get("brand");

        List<String> catIds = (List<String>) requestMap.get("categoryIds");
        Set<String> categoryIds = new HashSet<>(catIds);

        String supplierId = (String)requestMap.get("supplierId");

        String description = (String) requestMap.get("description");

        List<String> imageInfoIds = (List<String>)requestMap.get("itemImageInfoIds");
        Set<String> itemImageInfoIds = new HashSet<>(imageInfoIds);

        String stock = (String) requestMap.get("itemStock");
        Float itemStock = null;
        try {
            itemStock = Float.parseFloat(stock);
        }catch (Exception e){
            return new ApiResponseHandler("please provide valid values in itemStock can be integer or decimal", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }


        String sThreshold = (String) requestMap.get("stockThreshold");
        Float stockThreshold = null;
        try {
            stockThreshold = Float.parseFloat(sThreshold);
        }catch (Exception e){
            return new ApiResponseHandler("please provide valid values in stockThreshold can be integer or decimal", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }


        List<String> tutLinks = (List<String>) requestMap.get("tutorialLinks");
        Set<String> tutorialLinks = new HashSet(tutLinks);

        String skuCode = (String) requestMap.get("skuCode");
        String barcode = (String) requestMap.get("barcode");

        Boolean isReturnable = (Boolean) requestMap.get("isReturnable");
        Boolean isWarrantyAvailable = (Boolean) requestMap.get("isWarrantyAvailable");

        Integer warrantyYears = (Integer) requestMap.get("warrantyPeriodYears");
        Integer warrantyMonths = (Integer) requestMap.get("warrantyPeriodMonths");
        Integer warrantyDays = (Integer) requestMap.get("warrantyPeriodDays");
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
        item.setSkuCode(skuCode);
        item.setBarcode(barcode);
        item.setStockUnit(stockUnit);
        item.setIsReturnable(isReturnable);
        item.setIsWarrantyAvailable(isWarrantyAvailable);
        item.setWarrantyPeriod(period);
        item.setExpiryDate(expiryDate);

        return itemsApiService.addItemToStore(item);
    }

    @PostMapping("upload-item-images")
    public ApiResponseHandler uploadItemImages(@RequestPart("images")List<Multipart> itemImages){
        return null;
    }

}
