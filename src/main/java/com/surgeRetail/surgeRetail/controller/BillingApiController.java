package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.document.orderAndInvoice.Invoice;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.InvoiceItem;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.InvoiceTender;
import com.surgeRetail.surgeRetail.dtos.InvoiceRequestDto;
import com.surgeRetail.surgeRetail.service.BillingApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/billing")
public class BillingApiController {

    private final BillingApiService billingApiService;


    public BillingApiController(BillingApiService billingApiService) {
        this.billingApiService = billingApiService;
    }


    @PostMapping("/generate-invoice")     // for offline retail billing
    public ResponseEntity<ApiResponseHandler> generateInvoice(@RequestBody ApiRequestHandler apiRequestHandler){
        try {
            String storeId = apiRequestHandler.getStringValue("storeId");
            if (StringUtils.isEmpty(storeId))
                return ApiResponseHandler.createResponse("Please provide storeId", null, ResponseStatusCode.BAD_REQUEST);

            List<InvoiceItem> invoiceItems = apiRequestHandler.getListValue("invoiceItems", InvoiceItem.class);
            if (CollectionUtils.isEmpty(invoiceItems))
                return new ResponseEntity<>(new ApiResponseHandler("please provide Invoice Items", null,
                        com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus.BAD_REQUEST,
                        ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

//          checking if discountIds are available if discount value is present in invoice items
            boolean hasInvalidDiscount = invoiceItems.stream()
                    .anyMatch(item -> (item.getDiscountPerItem() != null &&
                            item.getDiscountPerItem().compareTo(BigDecimal.ZERO) > 0) &&
                            CollectionUtils.isEmpty(item.getDiscountIds()));

            if (hasInvalidDiscount) {
                return ApiResponseHandler.createResponse("please provide discountIds", null,
                        ResponseStatusCode.BAD_REQUEST);
            }

//          checking if taxIds are available if tax value is present in invoice items
            boolean hasInvalidTaxesInItemIds = invoiceItems.stream()
                    .anyMatch(item -> (item.getTaxPerItem() != null &&
                            item.getTaxPerItem().compareTo(BigDecimal.ZERO) > 0) &&
                            CollectionUtils.isEmpty(item.getTaxIds()));

            if (hasInvalidTaxesInItemIds) {
                return ApiResponseHandler.createResponse("please provide taxIds", null,
                        ResponseStatusCode.BAD_REQUEST);
            }

            String customerName = apiRequestHandler.getStringValue("customerName");
            String customerContactNo = apiRequestHandler.getStringValue("customerContactNo");

            BigDecimal discountOverTotalPrice = apiRequestHandler.getBigDecimalValue("discountOverTotalPrice");

            BigDecimal taxOverTotalPrice = apiRequestHandler.getBigDecimalValue("taxOverTotalPrice");
            if (taxOverTotalPrice == null)
                taxOverTotalPrice = BigDecimal.ZERO;

            if (discountOverTotalPrice == null)
                discountOverTotalPrice = BigDecimal.ZERO;

            String couponCode = apiRequestHandler.getStringValue("couponCode");

            String deliveryStatus = apiRequestHandler.getStringValue("deliveryStatus");
            if (deliveryStatus.isEmpty())
                return new ResponseEntity<>(new ApiResponseHandler("please provide delivery status", null, com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

            if (!deliveryStatus.equals(Invoice.DELIVERY_STATUS_NOT_DELIVERED) && !deliveryStatus.equals(Invoice.DELIVERY_STATUS_OUT_FOR_DELIVERY) && !deliveryStatus.equals(Invoice.DELIVERY_STATUS_DELIVERED))
                return new ResponseEntity<>(new ApiResponseHandler("deliveryStatus can only be " + Invoice.DELIVERY_STATUS_NOT_DELIVERED + ", "
                        + Invoice.DELIVERY_STATUS_OUT_FOR_DELIVERY + ", " + Invoice.DELIVERY_STATUS_DELIVERED, null, com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

            String paymentStatus = apiRequestHandler.getStringValue("paymentStatus");
            if (StringUtils.isEmpty(paymentStatus))
                return new ResponseEntity<>(new ApiResponseHandler("please provide payment status", null, com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true), HttpStatus.BAD_REQUEST);

            InvoiceTender invoiceTender = apiRequestHandler.getGenericObjectValue("invoiceTender", InvoiceTender.class);
            if (!paymentStatus.equals(Invoice.PAYMENT_STATUS_PENDING) && !paymentStatus.equals(Invoice.PAYMENT_STATUS_CANCELLED) && !paymentStatus.equals(Invoice.PAYMENT_STATUS_PAID) && !paymentStatus.equals(Invoice.PAYMENT_STATUS_PARTIAL))
                return new ResponseEntity<>(new ApiResponseHandler("paymentStatus can only be " + Invoice.PAYMENT_STATUS_PENDING + ", "
                        + Invoice.PAYMENT_STATUS_CANCELLED + ", "
                        + Invoice.PAYMENT_STATUS_PAID + ", "
                        + Invoice.PAYMENT_STATUS_PARTIAL, null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST,
                        true), HttpStatus.BAD_REQUEST);

            String generationType = apiRequestHandler.getStringValue("generationType");
            if (StringUtils.isEmpty(generationType))
                return ApiResponseHandler.createResponse("please provide generationType", null, ResponseStatusCode.BAD_REQUEST);

            if (!generationType.equals(Invoice.GENERATION_TYPE_OFFLINE) && !generationType.equals(Invoice.GENERATION_TYPE_ONLINE))
                return ApiResponseHandler.createResponse("GenerationType can only be Online or Off Line", null, ResponseStatusCode.BAD_REQUEST);
            BigDecimal grandTotal = apiRequestHandler.getBigDecimalValue("grandTotal");

            return billingApiService.generateInvoice(invoiceItems, taxOverTotalPrice, discountOverTotalPrice, customerName,
                    customerContactNo, couponCode, deliveryStatus, paymentStatus, grandTotal, storeId, invoiceTender);
        } catch (Exception e) {
            return ApiResponseHandler.createResponse("Error: "+e.getMessage(),null, ResponseStatusCode.BAD_REQUEST);
        }
    }

    @PostMapping("/list-invoice-by-filters")
    public ResponseEntity<ApiResponseHandler> listInvoiceByFilters(@RequestBody InvoiceRequestDto invoiceRequestDto){

        return billingApiService.listInvoiceByFilters(invoiceRequestDto);
    }

    @GetMapping("get-invoice-by-id/{invoiceId}")
    public ResponseEntity<ApiResponseHandler> getInvoiceById(@PathVariable String invoiceId){
        if (org.apache.commons.lang3.StringUtils.isEmpty(invoiceId))
            return ApiResponseHandler.createResponse("please provide InvoiceId", null, ResponseStatusCode.BAD_REQUEST);

        return billingApiService.invoiceByInvoiceId(invoiceId);
    }

    @GetMapping("/get-invoice-filters")
    public ResponseEntity<ApiResponseHandler> listInvoiceFilter(){
        return billingApiService.getInvoiceFilters();
    }
}
