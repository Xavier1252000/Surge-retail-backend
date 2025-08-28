package com.surgeRetail.surgeRetail.controller;

import com.surgeRetail.surgeRetail.dtos.InvoiceRequestDto;
import com.surgeRetail.surgeRetail.service.BillingApiService;
import com.surgeRetail.surgeRetail.utils.requestHandlers.ApiRequestHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing")
public class BillingApiController {

    private final BillingApiService billingApiService;


    public BillingApiController(BillingApiService billingApiService) {
        this.billingApiService = billingApiService;
    }

    @PostMapping("/list-invoice-by-filters")
    public ResponseEntity<ApiResponseHandler> listInvoiceByFilters(@RequestBody InvoiceRequestDto invoiceRequestDto){

        return billingApiService.listInvoiceByFilters(invoiceRequestDto);
    }

    @GetMapping("/get-invoice-filters")
    public ResponseEntity<ApiResponseHandler> listInvoiceFilter(){
        return billingApiService.getInvoiceFilters();
    }
}
