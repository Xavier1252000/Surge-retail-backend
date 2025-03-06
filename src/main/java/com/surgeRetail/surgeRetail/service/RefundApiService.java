package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.ReturnAndRefund.ReturnItems;
import com.surgeRetail.surgeRetail.document.ReturnAndRefund.ReturnRequest;
import com.surgeRetail.surgeRetail.repository.RefundApiRepository;
import com.surgeRetail.surgeRetail.utils.AppUtils;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class RefundApiService {

    private final RefundApiRepository refundApiRepository;

    public RefundApiService(RefundApiRepository refundApiRepository) {
        this.refundApiRepository = refundApiRepository;
    }

    public ApiResponseHandler createRefundRequest(String invoiceId, Set<ReturnItems> returnItems, String reason, Set<String> imageUrls, BigDecimal totalRefundAmount) {
        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setInvoiceId(invoiceId);
        returnRequest.setReturnItems(returnItems);
        returnRequest.setReason(reason);
        returnRequest.setImageUrls(imageUrls);
        returnRequest.setTotalRefundAmount(totalRefundAmount);
        returnRequest.setRefundStatus(ReturnRequest.REFUND_STATUS_INITIATED);
        refundApiRepository.createReturnRequest(returnRequest);

        ObjectNode node;
        try {
            node = AppUtils.mapObjectToObjectNode(returnRequest);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return new ApiResponseHandler("refund initiated successfully!!!", node, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }
}
