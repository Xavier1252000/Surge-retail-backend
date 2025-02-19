package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.orderAndInvoice.InvoiceItem;
import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import com.surgeRetail.surgeRetail.service.OrderApiService;
import com.surgeRetail.surgeRetail.utils.AuthenticatedUserDetails;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderApiController {
    private final OrderApiService orderApiService;

    public OrderApiController(OrderApiService orderApiService){
        this.orderApiService = orderApiService;
    }

    @PostMapping("/add-item-to-cart")
    public ApiResponseHandler addItemToCart(@RequestBody ApiRequestHandler requestHandler){
        String itemId = requestHandler.getStringValue("itemId");
        if (StringUtils.isEmpty(itemId))
            return new ApiResponseHandler("please provide itemId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Integer quantity = requestHandler.getIntegerValue("quantity");
        if (quantity == null || quantity <1)
            return new ApiResponseHandler("please provide quantity", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        return orderApiService.addItemToCart(itemId, quantity);
    }

    @GetMapping("/get-kart")
    public ApiResponseHandler getCart(){
        UserDetailsImpl userDetails = AuthenticatedUserDetails.getUserDetails();
        return orderApiService.getCart(userDetails.getId());
    }

    @PostMapping("/update-cart")
    public ApiResponseHandler updateCart(@RequestBody ApiRequestHandler apiRequestHandler){
        String itemId = apiRequestHandler.getStringValue("itemId");
        Integer quantity = apiRequestHandler.getIntegerValue("quantity");
        return null;
    }


    @PostMapping("/order-by-cart")
    public ApiResponseHandler placeOrder(@RequestBody ApiRequestHandler apiRequestHandler){
        String shippingAddressId = apiRequestHandler.getStringValue("shippingAddressId");
        if (StringUtils.isEmpty(shippingAddressId))
            return new ApiResponseHandler("please provide shipping address", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Boolean termsAndCondition = apiRequestHandler.getBooleanValue("termsAndCondition");
        if (termsAndCondition == null)
            return new ApiResponseHandler("please agree with the terms&conditions", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true );

        Boolean confirmShippingAddress = apiRequestHandler.getBooleanValue("confirmShippingAddress");
        if (confirmShippingAddress == null)
            return new ApiResponseHandler("please confirm the shipping address", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        return orderApiService.orderByCart(shippingAddressId, termsAndCondition, confirmShippingAddress);
    }

    @PostMapping("/direct-order")   // applicable for only single type of items can have more than one quantity
    public ApiResponseHandler directOrder(@RequestBody ApiRequestHandler apiRequestHandler){
        String itemId = apiRequestHandler.getStringValue("itemId");
        if (StringUtils.isEmpty(itemId))
            return new ApiResponseHandler("please provide itemId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Integer quantity = apiRequestHandler.getIntegerValue("quantity");
        if (quantity == null || quantity <1)
            return new ApiResponseHandler("quantity can't be less than one", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        String shippingAddressId = apiRequestHandler.getStringValue("shippingAddressId");
        return orderApiService.directOrder(itemId, quantity, shippingAddressId);
    }

    @PostMapping("/confirm-order")
    public ApiResponseHandler confirmOrder(@RequestBody ApiRequestHandler apiRequestHandler){
        return null;
    }

    @PostMapping("update-order-status")
    public ApiRequestHandler updateOrderStatus(@RequestBody ApiRequestHandler apiRequestHandler){
        return null;
    }

    @PostMapping("/save-invoice-items")
    public ApiResponseHandler saveInvoiceItems(@RequestBody ApiRequestHandler apiRequestHandler){
        String itemId = apiRequestHandler.getStringValue("itemId");
        if (StringUtils.isEmpty(itemId))
            return new ApiResponseHandler("please provide itemId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Integer quantity = apiRequestHandler.getIntegerValue("quantity");
        BigDecimal rate = apiRequestHandler.getBigDecimalValue("rate");
        BigDecimal totalPrice = apiRequestHandler.getBigDecimalValue("totalPrice");
        return orderApiService.saveInvoiceItem(itemId, quantity, rate, totalPrice);
    }

    @PostMapping("/generate-invoice")     // for offline retail billing
    public ApiResponseHandler generateInvoice(@RequestBody ApiRequestHandler apiRequestHandler){
        List<InvoiceItem> invoiceItems = apiRequestHandler.getListValue("invoiceItems", InvoiceItem.class);
        String customerName = apiRequestHandler.getStringValue("customerName");
        String customerContactNo = apiRequestHandler.getStringValue("customerContactNo");
        return orderApiService.generateInvoice(invoiceItems, customerName, customerContactNo);
    }

}
