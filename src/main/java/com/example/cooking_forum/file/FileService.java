package com.example.cooking_forum.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService {
    private  final Cloudinary cloudinary;

    public ImageInfo uploadImageToCloud(MultipartFile file)throws IOException{
        try{
            if(file.isEmpty()){
                throw new IllegalArgumentException("Uploaded file is empty");
            }

            Map result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder","recipes"));

            String imagePath = result.get("secure_url").toString();
            String imageId = result.get("public_id").toString();

            return new ImageInfo(imagePath, imageId);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Cloudinary upload failer");
        }

    }

    public ImageInfo replaceImage(String oldImageId, MultipartFile newImage) throws IOException {
        try{
            Map result = cloudinary.uploader().upload(newImage.getBytes(),
                    ObjectUtils.asMap("folder","recipes"));

            if(result != null && result.containsKey("public_id")){
                if (oldImageId != null && !oldImageId.isBlank()) {
                    cloudinary.uploader().destroy(oldImageId, ObjectUtils.emptyMap());
                }
            }

            String imagePath = result.get("secure_url").toString();
            String imageId = result.get("public_id").toString();

            return new ImageInfo(imagePath,imageId);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Image replace failed",e);
        }

    }

    public void deleteImage(String imageId) throws IOException {
        if (imageId != null && !imageId.isBlank()) {
            cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
        }
    }
}
