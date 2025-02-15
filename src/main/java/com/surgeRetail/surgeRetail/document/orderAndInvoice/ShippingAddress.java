package com.surgeRetail.surgeRetail.document.orderAndInvoice;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.stream.Location;

@Data
@Document
public class ShippingAddress extends Auditable{
    @Id
    private String id;
    private String userId;
    private String description;
    private String addNumbering;  //houseno shop no store no etc
    private String city;
    private String pinCode;
    private String state;
    private String country;
    private GeoLocation geoLocation;
}
