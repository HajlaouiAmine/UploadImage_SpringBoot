package com.SmartIT.FirstProject.controller;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.SmartIT.FirstProject.entity.Image;
import com.SmartIT.FirstProject.entity.NotAnImageFileException;
import com.SmartIT.FirstProject.service.FileStorageService;
import com.SmartIT.FirstProject.serviceImpl.ImageServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/upload")
public class ImageUploadController {
	
    private final FileStorageService fileStorageService;
    private final ImageServiceImpl imageServiceImpl;


    @PostMapping("/add")
    public ResponseEntity<?> saveImage(
            @RequestParam(name = "image") String image,
            @RequestParam(value = "imageUrl", required = false) MultipartFile imageUrl

    ) throws IOException, NotAnImageFileException {
       
        Image imageToSave = new ObjectMapper().readValue(image, Image.class);

        if (imageUrl != null) {
            imageServiceImpl.saveImageUrl(imageToSave, imageUrl);
        }
        Image result = imageServiceImpl.saveImage(imageToSave);

        if (result != null) {

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>("Problem with adding banner", HttpStatus.BAD_REQUEST);
    }
    
	
	 @GetMapping(path = "/image/{fileName}", produces = IMAGE_JPEG_VALUE)
	    public byte[] getPhotoPrincipaleBanner( @PathVariable("fileName") String fileName) throws IOException {
	        return Files.readAllBytes(Paths.get("uploads/users" +"/"+ fileName));
	    }
}
