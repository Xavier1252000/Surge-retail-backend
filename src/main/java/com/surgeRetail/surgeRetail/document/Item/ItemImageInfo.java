package com.surgeRetail.surgeRetail.document.Item;

import com.surgeRetail.surgeRetail.utils.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class ItemImageInfo extends Auditable {

    @Id
    private String id;
    private String itemId;
    private String imageUrl;
    private Map<String, Object> imgUploadResponse;    // for uploading to external services like cloudinary
}
