package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surgeRetail.surgeRetail.document.master.DiscountMaster;
import com.surgeRetail.surgeRetail.document.master.TaxMaster;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.Invoice;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.InvoiceItem;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.InvoiceTender;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.dtos.InvoiceRequestDto;
import com.surgeRetail.surgeRetail.dtos.ItemNameDto;
import com.surgeRetail.surgeRetail.repository.BillingApiRepository;
import com.surgeRetail.surgeRetail.repository.ItemsApiRepository;
import com.surgeRetail.surgeRetail.repository.MasterApiRepository;
import com.surgeRetail.surgeRetail.repository.UserApiRepository;
import com.surgeRetail.surgeRetail.utils.generic.GenericFilterService;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillingApiService {

    private final GenericFilterService genericFilterService;
    private final ObjectMapper objectMapper;
    private final BillingApiRepository billingApiRepository;
    private final ItemsApiRepository itemsApiRepository;
    private final MasterApiRepository masterApiRepository;
    private final UserApiRepository userApiRepository;

    public BillingApiService(GenericFilterService genericFilterService,
                             ObjectMapper objectMapper,
                             BillingApiRepository billingApiRepository,
                             ItemsApiRepository itemsApiRepository,
                             MasterApiRepository masterApiRepository,
                             UserApiRepository userApiRepository) {
        this.genericFilterService = genericFilterService;
        this.objectMapper = objectMapper;
        this.billingApiRepository = billingApiRepository;
        this.itemsApiRepository = itemsApiRepository;
        this.masterApiRepository = masterApiRepository;
        this.userApiRepository = userApiRepository;
    }

    @Transactional
    public ResponseEntity<ApiResponseHandler> generateInvoice(List<InvoiceItem> invoiceItems, BigDecimal taxOverTotalPrice,
                                                              BigDecimal discountOverTotalPrice, String customerName,
                                                              String customerContactNo, String couponCode, String deliveryStatus,
                                                              String paymentStatus, BigDecimal grandTotal,
                                                              String storeId, InvoiceTender invoiceTender){
        Invoice invoice = new Invoice();
        invoice.setCustomerName(customerName);
        invoice.setCustomerContactNo(customerContactNo);
        invoice.setStoreId(storeId);

        Invoice lastInvoice = billingApiRepository.getGreatestSerialNoInvoice(storeId);
        Long serialNo = Objects.nonNull(lastInvoice)?
                lastInvoice.getSerialNo():null;
        invoice.setSerialNo(serialNo == null ? 1L: serialNo+1L);

        invoice.setGrossAmount(invoiceItems.stream().map(InvoiceItem::getTotalBasePrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        invoice.setNetAmount(invoiceItems.stream().map(InvoiceItem::getFinalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));

        invoice.setInvoiceTaxAmount(taxOverTotalPrice);
        invoice.setInvoiceDiscountAmount(discountOverTotalPrice);

        List<DiscountMaster> activeInvoiceDiscounts = masterApiRepository.findActiveInvoiceDiscounts(couponCode);
        List<String> activeInvoiceDiscountIds = activeInvoiceDiscounts.stream().map(x -> x.getId()).toList();
        invoice.setInvoiceDiscountIds(activeInvoiceDiscountIds);

        List<TaxMaster> activeInvoiceTaxMaster = masterApiRepository.findActiveInvoiceTaxMaster();
        List<String> activeInvoiceTaxMasterIds = activeInvoiceTaxMaster.stream().map(x -> x.getId()).toList();
        invoice.setInvoiceTaxIds(activeInvoiceTaxMasterIds);
        invoice.setPaymentStatus(paymentStatus);
        invoice.setDeliveryStatus(deliveryStatus);

        invoice.setGrandTotal(grandTotal);
        invoice.setInvoiceTender(invoiceTender==null?new InvoiceTender():invoiceTender);
        invoice.onCreate();
        Invoice savedInvoice = billingApiRepository.saveInvoice(invoice);

        billingApiRepository.reduceItemsStock(invoiceItems);

        invoiceItems.forEach(e->{
            e.setInvoiceId(savedInvoice.getId());
        });

        List<InvoiceItem> savedInvoiceItems = billingApiRepository.saveAllInvoiceItem(invoiceItems);
        ObjectNode node = objectMapper.createObjectNode();
        node.set("invoice", objectMapper.valueToTree(savedInvoice));
        node.set("invoiceItems", objectMapper.valueToTree(savedInvoiceItems));

        return new ResponseEntity<>(new ApiResponseHandler("invoice generated successfully", node, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false), HttpStatus.CREATED);
    }

        public ResponseEntity<ApiResponseHandler> listInvoiceByFilters(InvoiceRequestDto invoiceFilters) {

            objectMapper.registerModule(new JavaTimeModule());

            Map<String, Object> filterMap = new HashMap<>();
            Field[] fields = InvoiceRequestDto.class.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(invoiceFilters);
                    if (value != null) {
                        filterMap.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field: " + field.getName(), e);
                }
            }

            List<Invoice> invoices = genericFilterService.getObjectByFieldFilters(Invoice.class, filterMap);
            return ApiResponseHandler.createResponse("Success", invoices, ResponseStatusCode.SUCCESS);
        }


    public ResponseEntity<ApiResponseHandler> invoiceByInvoiceId(String invoiceId) {
        Invoice invoice = billingApiRepository.invoiceByInvoiceId(invoiceId);
        if (invoice==null)
            return ApiResponseHandler.createResponse("No invoice found", null, ResponseStatusCode.BAD_REQUEST);
        Document storeById = billingApiRepository.getStoreById(invoice.getStoreId());
        List<InvoiceItem> invoiceItems = billingApiRepository.findInvoiceItemByStoreId(invoice.getId());
        List<String> ivIds = invoiceItems.stream().map(InvoiceItem::getItemId).toList();
        List<ItemNameDto> itemsById = itemsApiRepository.getItemsById(ivIds);

        Set<String> discountIds = invoiceItems.stream().map(InvoiceItem::getDiscountIds).flatMap(Collection::stream).collect(Collectors.toSet());
        Set<String> taxIds = invoiceItems.stream().map(InvoiceItem::getTaxIds).flatMap(Collection::stream).collect(Collectors.toSet());;
        List<DiscountMaster> ivItemsDiscounts = masterApiRepository.getDiscountMasterByIds(discountIds);
        List<TaxMaster> ivItemsTaxes = masterApiRepository.getTaxMasterByIds(taxIds);
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        ObjectNode node = objectMapper.createObjectNode();
        User userById = userApiRepository.findUserById(invoice.getCreatedBy());
        ObjectNode invoiceNode = objectMapper.convertValue(invoice, ObjectNode.class);
        invoiceNode.put("storeName", Objects.nonNull(storeById)?storeById.get("storeName", "store not found"):"store " +
                "not found");
        if (userById != null)
            invoiceNode.put("createdByName", userById.getFirstName()+" "+userById.getLastName());
        node.set("invoice", invoiceNode);

        ArrayNode invoiceItemsRoot = objectMapper.createArrayNode();
        for (InvoiceItem i:invoiceItems){
            ObjectNode invNode = objectMapper.createObjectNode();
            invNode.put("id", i.getId());
            invNode.put("itemId", i.getItemId());
            Optional<ItemNameDto> item = itemsById.stream().filter(x -> x.getId().equals(i.getItemId())).findFirst();
            invNode.put("itemName", item.isPresent()? item.get().getItemName():"Item not found");
            invNode.put("quantity", i.getQuantity());
            invNode.put("itemBasePrice", i.getItemBasePrice());
            invNode.put("totalBasePrice", i.getTotalBasePrice());
            invNode.set("discountIds", objectMapper.valueToTree(i.getDiscountIds()));
            invNode.set("discountDetails", objectMapper.valueToTree(ivItemsDiscounts.stream()
                    .filter(x->i.getDiscountIds()
                            .contains(x.getId())).collect(Collectors.toSet())));
            invNode.put("discountPerItem", i.getDiscountPerItem());
            invNode.put("totalDiscount", i.getTotalDiscount());
            invNode.set("taxIds", objectMapper.valueToTree(i.getTaxIds()));
            invNode.set("taxDetails", objectMapper.valueToTree(ivItemsTaxes.stream()
                            .filter(x->i.getTaxIds().contains(x.getId()))
                            .collect(Collectors.toSet())));
            invNode.put("taxPerItem", i.getTaxPerItem());
            invNode.put("totalTax", i.getTotalTax());
            invNode.put("finalPricePerItem", i.getFinalPricePerItem());
            invNode.put("finalPrice",i.getFinalPrice());
            invoiceItemsRoot.add(invNode);
        }

        node.set("invoiceItems", objectMapper.valueToTree(invoiceItemsRoot));
        return ApiResponseHandler.createResponse("Success", node, ResponseStatusCode.SUCCESS);
    }

    public ResponseEntity<ApiResponseHandler> getInvoiceFilters() {
        ArrayList<String> root = new ArrayList<>();
        Field[] declaredFields = InvoiceRequestDto.class.getDeclaredFields();
        for (Field f:declaredFields){
            root.add(f.getName());
        }
        return ApiResponseHandler.createResponse("success", root, ResponseStatusCode.SUCCESS);
    }
}
