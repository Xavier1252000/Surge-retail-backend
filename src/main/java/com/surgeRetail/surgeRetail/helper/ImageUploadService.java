package com.surgeRetail.surgeRetail.helper;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ImageUploadService {

    @Value("${CLOUDINARY_URL}")
    String cloudinaryUrl;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/file")
    public List<Map<String, Object>> imagesUpload(List<File> files) throws IOException {
        List<Map<String, Object>> response = new ArrayList<>();
        try {
            for (File file : files) {
                Map params = ObjectUtils.asMap(
                        "public_id", file.getName(),
                        "overwrite", true,
                        "notification_url", "",
                        "resource_type", "image"
                );
                Map upload = cloudinary.uploader().upload(file, params);
                response.add(upload);
            }
        } catch (Exception E) {
            return null;
        }
        return response;
    }


    public List<Map<String, Object>> uploadMultipartFiles(List<MultipartFile> multipartFiles){
        List<Map<String, Object>> response = new ArrayList<>();
        try {
            for (MultipartFile file : multipartFiles) {
                Map params = ObjectUtils.asMap(
                        "public_id", file.getOriginalFilename(),
                        "overwrite", true,
                        "notification_url", "",
                        "resource_type", "image"
                );
                Map<String, Object> upload = cloudinary.uploader().upload(file.getBytes(), params);
                response.add(upload);
            }
        } catch (Exception E) {
            return null;
        }
        return response;
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









//   convert file to multipart file but saves file locally

    //        try {
//
//            for (MultipartFile m : file){
//                File convFile = new File(Objects.requireNonNull(m.getOriginalFilename()));
//                FileOutputStream fos = new FileOutputStream(convFile);
//                fos.write(m.getBytes());
//                fos.close();
//                images.add(convFile);
//        }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
}

