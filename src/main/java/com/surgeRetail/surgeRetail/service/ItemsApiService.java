package com.surgeRetail.surgeRetail.service;

import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.document.master.DiscountMaster;
import com.surgeRetail.surgeRetail.document.master.TaxMaster;
import com.surgeRetail.surgeRetail.repository.ItemsApiRepository;
import com.surgeRetail.surgeRetail.repository.MasterApiRepository;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Service
public class ItemsApiService {
    private final ItemsApiRepository itemsApiRepository;
    private final MasterApiRepository masterApiRepository;

    public ItemsApiService(ItemsApiRepository itemsApiRepository,
                           MasterApiRepository masterApiRepository){
        this.itemsApiRepository = itemsApiRepository;
        this.masterApiRepository = masterApiRepository;
    }
    public ApiResponseHandler addItemToStore(Item item) {

        if (item.getProfitToGainInPercentage() != null) {
            BigDecimal profitMargin = item.getProfitToGainInPercentage()
                    .multiply(item.getCostPrice())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP) // Higher precision
                    .setScale(2, RoundingMode.HALF_UP); // Final rounding to 2 decimals

            System.out.println("Profit Margin: " + profitMargin);
            item.setBaseSellingPrice(item.getCostPrice().add(profitMargin).setScale(2, RoundingMode.HALF_UP));
        } else {
            BigDecimal profitToGainPercentage = item.getBaseSellingPrice()
                    .subtract(item.getCostPrice())
                    .divide(item.getCostPrice(), 4, RoundingMode.HALF_UP) // Higher precision
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP); // Final rounding to 2 decimals

            System.out.println("Profit to Gain Percentage: " + profitToGainPercentage);
            item.setProfitToGainInPercentage(profitToGainPercentage);
        }

        Set<String> applicableTaxMasterIds = item.getApplicableTaxes();
        List<TaxMaster> taxMasterByIds = masterApiRepository.findTaxMasterByIds(applicableTaxMasterIds);
        BigDecimal totalTaxPrice = BigDecimal.valueOf(0);
        for (TaxMaster t:taxMasterByIds){
            BigDecimal taxPercentage = t.getTaxPercentage();
            BigDecimal taxPrice = (item.getBaseSellingPrice().multiply(taxPercentage)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalTaxPrice = totalTaxPrice.add(taxPrice);
        }
        item.setTotalTaxPrice(totalTaxPrice);

        Set<String> discountMasterIds = item.getDiscountMasterIds();
        List<DiscountMaster> discountMasterByIds = masterApiRepository.findDiscountMasterByIds(discountMasterIds);
        BigDecimal totalDiscountPrice = null;
        for (DiscountMaster d:discountMasterByIds){
            BigDecimal discountPercentage = d.getDiscountPercentage();
            BigDecimal discountPrice = (item.getBaseSellingPrice().multiply(discountPercentage)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalDiscountPrice = item.getTotalDiscountPrice().add(discountPrice);
        }
        item.setTotalDiscountPrice(totalDiscountPrice);

        item.setFinalPrice(item.getBaseSellingPrice()
                .add(item.getTotalTaxPrice())
                .add(item.getAdditionalPrice())
                .subtract(item.getTotalDiscountPrice()!=null?item.getTotalDiscountPrice():BigDecimal.ZERO));

        item.setProfitMargin(item.getFinalPrice().subtract(item.getTotalTaxPrice()).subtract(item.getTotalDiscountPrice()!=null?item.getTotalDiscountPrice():BigDecimal.ZERO).subtract(item.getCostPrice()).setScale(2, RoundingMode.HALF_UP));


        item.setMarkupPercentage(item.getProfitMargin().divide(item.getCostPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));

        itemsApiRepository.saveItem(item);


        return new ApiResponseHandler("item added successfully to store", item, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);

    }
}
