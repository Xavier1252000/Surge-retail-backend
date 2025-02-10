package com.surgeRetail.surgeRetail.service;

import com.surgeRetail.surgeRetail.document.Item.Item;
import com.surgeRetail.surgeRetail.repository.ItemsApiRepository;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.stereotype.Service;

@Service
public class ItemsApiService {
    private final ItemsApiRepository itemsApiRepository;

    public ItemsApiService(ItemsApiRepository itemsApiRepository){
        this.itemsApiRepository = itemsApiRepository;
    }
    public ApiResponseHandler addItemToStore(Item item) {

        return new ApiResponseHandler("item added successfully to store", item, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }
}
