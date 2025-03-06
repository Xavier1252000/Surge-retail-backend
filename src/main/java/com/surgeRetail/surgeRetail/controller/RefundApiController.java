package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.ReturnAndRefund.ReturnItems;
import com.surgeRetail.surgeRetail.repository.RefundApiRepository;
import com.surgeRetail.surgeRetail.service.RefundApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("/refund")
public class RefundApiController {

    private final RefundApiService refundApiService;

    public RefundApiController(RefundApiService refundApiService){
        this.refundApiService = refundApiService;
    }

    @PostMapping("/create-refund-request")
    public ApiResponseHandler createRefundRequest(@RequestBody ApiRequestHandler apiRequestHandler){
        String invoiceId = apiRequestHandler.getStringValue("invoiceId");
        if (StringUtils.isEmpty(invoiceId))
            return new ApiResponseHandler("please provide invoiceId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Set<ReturnItems> returnItems = apiRequestHandler.getSetValue("returnItems", ReturnItems.class);

        String reason = apiRequestHandler.getStringValue("reason");
        if (StringUtils.isEmpty(reason))
            return new ApiResponseHandler("please provide reason for return", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Set<String> imageUrls = apiRequestHandler.getSetValue("imageUrls", String.class);
        BigDecimal totalRefundAmount = apiRequestHandler.getBigDecimalValue("totalRefundAmount");

        return refundApiService.createRefundRequest(invoiceId, returnItems, reason, imageUrls, totalRefundAmount );
    }
}
