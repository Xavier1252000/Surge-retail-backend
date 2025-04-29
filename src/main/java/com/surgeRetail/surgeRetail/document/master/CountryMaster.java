package com.surgeRetail.surgeRetail.document.master;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document
public class CountryMaster {
    private String id;
    private String name;
    private String currency;
    private String code;     //currencyCode
    private String symbol;
    private String language;
    private String callingCode;

    public CountryMaster(String name, String currency, String code, String symbol, String callingCode) {
        this.name = name;
        this.currency = currency;
        this.code = code;
        this.symbol = symbol;
        this.callingCode = callingCode;
    }




}
