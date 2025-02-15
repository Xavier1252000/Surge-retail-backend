package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.Cart;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.CartItem;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.Order;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.ShippingAddress;
import com.surgeRetail.surgeRetail.repository.ItemsApiRepository;
import com.surgeRetail.surgeRetail.repository.OrderApiRepository;
import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import com.surgeRetail.surgeRetail.utils.AuthenticatedUserDetails;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class OrderApiService {
    private final OrderApiRepository orderApiRepository;
    private final ItemsApiRepository itemsApiRepository;
    private final ObjectMapper objectMapper;

    public OrderApiService(OrderApiRepository orderApiRepository,
                           ItemsApiRepository itemsApiRepository,
                           ObjectMapper objectMapper){
        this.orderApiRepository = orderApiRepository;
        this.itemsApiRepository = itemsApiRepository;
        this.objectMapper = objectMapper;
    }
    public ApiResponseHandler addItemToCart(String itemId, Integer quantity) {

        Item itemById = itemsApiRepository.getItemById(itemId);
        if (itemById==null)
            return new ApiResponseHandler("item currently unavailable", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

//      Extracting customer id from the security context holder
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String customerId = userDetails.getId();
        Cart cart = orderApiRepository.findCartByCustomerId(customerId);
        if (cart == null) {
            cart = new Cart();
            cart.onCreate();
        }

        CartItem cartItem = new CartItem();
        cartItem.setItemId(itemId);
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(itemById.getBaseSellingPrice());
        cartItem.setTotalCostPrice(itemById.getCostPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItem.setTotalBaseSellingPrice(itemById.getBaseSellingPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItem.setTotalTaxPrice(itemById.getTotalTaxPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItem.setTotalDiscountPrice(itemById.getTotalDiscountPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItem.setFinalPrice(cartItem.getTotalBaseSellingPrice().add(cartItem.getTotalTaxPrice())
                .add(cartItem.getTotalAdditionalPrice()).subtract(cartItem.getTotalDiscountPrice()));
        cartItem.onCreate();

        cart.getCartItems().add(cartItem);
        cart.setCustomerId(customerId);
        cart.setTotalDiscount(BigDecimal.ZERO);
        cart.setDiscountPercentage(BigDecimal.ZERO);
        cart.setTaxOnFinalPrice(BigDecimal.ZERO);
        cart.setTotalPriceBeforeDiscount(cart.getCartItems().stream().map(x->x.getTotalBaseSellingPrice().add(x.getTotalTaxPrice()).add(x.getTotalAdditionalPrice())).reduce(BigDecimal.ZERO, BigDecimal::add));
        cart.setTotalPriceWithDiscount(cart.getTotalPriceBeforeDiscount().subtract(cart.getTotalDiscount()));
        cart.onUpdate();
        orderApiRepository.saveCart(cart);
        ObjectNode node = objectMapper.createObjectNode();
        node.put("cartId", cart.getId());
        node.put("customerId", cart.getCustomerId());
        node.put("totalPriceBeforeDiscount", cart.getTotalPriceBeforeDiscount());
        node.put("discountPercentage", cart.getDiscountPercentage());
        node.put("taxOnFinalPrice", cart.getTaxOnFinalPrice());
        node.put("totalDiscount", cart.getTotalDiscount());
        node.put("totalPriceWithDiscount", cart.getTotalPriceWithDiscount());
        node.put("cartItems", objectMapper.valueToTree(cart.getCartItems()));
        node.put("createdOn", String.valueOf(cart.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(cart.getModifiedOn()));
        node.put("active", cart.getActive());

        return new ApiResponseHandler("item added to cart!!!", node, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }


    public ApiResponseHandler getCart(String id) {
        Cart cart = orderApiRepository.findCartByCustomerId(id);
        ObjectNode node = objectMapper.createObjectNode();
        node.put("cartId", cart.getId());
        node.put("customerId", cart.getCustomerId());
        node.put("totalPriceBeforeDiscount", cart.getTotalPriceBeforeDiscount());
        node.put("discountPercentage", cart.getDiscountPercentage());
        node.put("taxOnFinalPrice", cart.getTaxOnFinalPrice());
        node.put("totalDiscount", cart.getTotalDiscount());
        node.put("totalPriceWithDiscount", cart.getTotalPriceWithDiscount());
        node.put("cartItems", objectMapper.valueToTree(cart.getCartItems()));
        node.put("createdOn", String.valueOf(cart.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(cart.getModifiedOn()));
        node.put("active", cart.getActive());
        return new ApiResponseHandler("Proceed to order", node, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }


    public ApiResponseHandler orderByCart(String shippingAddressId, boolean termsAndCondition, boolean confirmShippingAddress) {
        UserDetailsImpl userDetails = AuthenticatedUserDetails.getUserDetails();
        String id = userDetails.getId();

        ShippingAddress shippingAddress = orderApiRepository.getShippingAddressById(shippingAddressId);
        if (shippingAddress == null)
            return new ApiResponseHandler("please provide shipping address", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        Cart cartByCustomerId = orderApiRepository.findCartByCustomerId(id);
        if (CollectionUtils.isEmpty(cartByCustomerId.getCartItems()))
            return new ApiResponseHandler("add items to cart before order placement", cartByCustomerId, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomerId(id);
        order.setCustomerEmailId(userDetails.getUser().getEmailId());
        order.setOrderStatus(Order.ORDER_STATUS_REQUESTED);
        order.setTotalTaxPrice(BigDecimal.ZERO);
        order.setFinalAmount(cartByCustomerId.getTotalPriceWithDiscount());
        order.setAcceptedTerms(true);
        order.setCustomerMobileNo(userDetails.getUser().getMobileNo());
        order.setItemIds(cartByCustomerId.getCartItems().stream().map(x->x.getItemId()).collect(Collectors.toSet()));
        order.setCustomerFullName(userDetails.getUser().getFirstName() + " " +userDetails.getUser().getLastName());
        order.setShippingAddress(shippingAddress);
        order.onCreate();

        orderApiRepository.saveOrder(order);

        ObjectNode node = objectMapper.createObjectNode();
        node.put("orderId",order.getId());
        node.put("orderNo.",order.getOrderNumber());
        node.put("customerId",order.getCustomerId());
        node.put("emailId",order.getCustomerEmailId());
        node.put("contactNo",order.getCustomerMobileNo());
        node.set("items",objectMapper.valueToTree(cartByCustomerId.getCartItems()));
        node.put("orderStatus", order.getOrderStatus());
        node.set("shippingAddress", objectMapper.valueToTree(order.getShippingAddress()));
        node.put("createdOn", String.valueOf(order.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(order.getModifiedOn()));
        node.put("createdBy",order.getCreatedBy());
        node.put("modifiedBy",order.getModifiedBy());
        node.put("active", order.getActive());
        return new ApiResponseHandler("order request sent to sellers", node, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler directOrder(String itemId, Integer quantity ,String shippingAddressId) {
        UserDetailsImpl userDetails = AuthenticatedUserDetails.getUserDetails();
        String id = userDetails.getId();

        ShippingAddress shippingAddress = orderApiRepository.getShippingAddressById(shippingAddressId);
        if (shippingAddress == null)
            return new ApiResponseHandler("please provide shipping address", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomerId(id);
        order.setCustomerEmailId(userDetails.getUser().getEmailId());
        order.setOrderStatus(Order.ORDER_STATUS_REQUESTED);
        order.setTotalTaxPrice(BigDecimal.ZERO);

        Set<String> itemIds  = new HashSet<>();
        itemIds.add(itemId);
        order.setItemIds(itemIds);
        order.setAcceptedTerms(true);
        order.setCustomerMobileNo(userDetails.getUser().getMobileNo());
        order.setCustomerFullName(userDetails.getUser().getFirstName() + " " +userDetails.getUser().getLastName());
        order.setShippingAddress(shippingAddress);
        order.onCreate();

        orderApiRepository.saveOrder(order);

        ObjectNode node = objectMapper.createObjectNode();
        node.put("orderId",order.getId());
        node.put("orderNo.",order.getOrderNumber());
        node.put("customerId",order.getCustomerId());
        node.put("emailId",order.getCustomerEmailId());
        node.put("contactNo",order.getCustomerMobileNo());
        node.put("orderStatus", order.getOrderStatus());
        node.set("shippingAddress", objectMapper.valueToTree(order.getShippingAddress()));
        node.put("createdOn", String.valueOf(order.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(order.getModifiedOn()));
        node.put("createdBy",order.getCreatedBy());
        node.put("modifiedBy",order.getModifiedBy());
        node.put("active", order.getActive());
        return new ApiResponseHandler("order request sent to sellers", node, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);

    }
}
