package com.example.cooking_forum.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> saveImage(@RequestParam("image") MultipartFile file) throws IOException {
        try{
            return ResponseEntity.ok(fileService.uploadImageToCloud(file));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Cloudinary API Error" + e.getMessage());
        }
    }

    @PostMapping("/replace")
    public ResponseEntity<?> replaceImage(@RequestParam("oldImageId") String oldImageId,
                                          @RequestParam("image") MultipartFile newImage) throws IOException {
        try{
            return  ResponseEntity.ok(fileService.replaceImage(oldImageId, newImage));
        }catch (IOException ioe){
            return  ResponseEntity.status(500).body("Network or file  error.");
        }catch (Exception e){
            e.printStackTrace();
           return ResponseEntity.status(500).body("Cloudinary API error. Image upload failed.");
        }
    }
}
