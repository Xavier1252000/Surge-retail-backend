package com.surgeRetail.surgeRetail.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadApiController {

    @Value("${CLOUDINARY_URL}")
    String cloudinaryUrl;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/file")
    public Map<String, Object> imageUpload() throws IOException {
        File file = new File("/home/nikhil-shukla/Downloads/icons8-chat-94.png");

        Map params = ObjectUtils.asMap(
                "public_id", "a.txt",
                "overwrite", true,
                "notification_url", "",
                "resource_type", "image"
        );
        return cloudinary.uploader().upload(file, params);
    }





//    public File getTempFile(MultipartFile multipartFile)
//    {
//        CommonsMultipartFile commonsMultipartFile = (CommonsMultipartFile) multipartFile;
//        FileItem fileItem = commonsMultipartFile.getFileItem();
//        DiskFileItem diskFileItem = (DiskFileItem) fileItem;
//        String absPath = diskFileItem.getStoreLocation().getAbsolutePath();
//        File file = new File(absPath);
//
//        //trick to implicitly save on disk small files (<10240 bytes by default)
//        if (!file.exists()) {
//            file.createNewFile();
//            multipartFile.transferTo(file);
//        }
//
//        return file;
//    }
}
