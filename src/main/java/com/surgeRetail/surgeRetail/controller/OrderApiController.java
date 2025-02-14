package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import com.surgeRetail.surgeRetail.service.OrderApiService;
import com.surgeRetail.surgeRetail.utils.AuthenticatedUserDetails;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/place-order")
    public ApiResponseHandler placeOrder(){
        return null;
    }

}
