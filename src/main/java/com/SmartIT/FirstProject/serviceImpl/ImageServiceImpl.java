package com.SmartIT.FirstProject.serviceImpl;


import static com.SmartIT.FirstProject.constant.FileConstant.DOT;
import static com.SmartIT.FirstProject.constant.FileConstant.FORWARD_SLASH;
import static com.SmartIT.FirstProject.constant.FileConstant.JPG_EXTENSION;
import static com.SmartIT.FirstProject.constant.FileConstant.NOT_AN_IMAGE_FILE;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.SmartIT.FirstProject.entity.Image;
import com.SmartIT.FirstProject.entity.NotAnImageFileException;
import com.SmartIT.FirstProject.repo.ImageRepo;
import com.SmartIT.FirstProject.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ImageServiceImpl {

	
	   public static final String PRODUIT_PATH = "/api/upload/image";
	    public static final String JPG_EXTENSION = "jpg";
	    public static final String PRODUIT_FOLDER = System.getProperty("uploads") + "/imageUrl/";
	    public static final String DIRECTORY_CREATED = "Created directory for: ";
	    public static final String FILE_SAVED_IN_FILE_SYSTEM = "Saved file in file system by name: ";
	    public static final String DOT = ".";
	    public static final String FORWARD_SLASH = "/";
	    public static final String NOT_AN_IMAGE_FILE = " is not an image file. Please upload an image file";
    @Value("${upload.directory}")
    public static final String PATH = "/api/upload/";

   
    private final ImageRepo imageRepository;
    private final FileStorageService fileStorageService;
   

   public void saveImageUrl(Image image, MultipartFile file) throws IOException, NotAnImageFileException {
       if (file != null) {
           if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(file.getContentType())) {
               throw new NotAnImageFileException(file.getOriginalFilename() + NOT_AN_IMAGE_FILE);
           }
           Path produitFolder = Paths.get(PRODUIT_FOLDER + image.getName()).toAbsolutePath().normalize();
           if(!Files.exists(produitFolder)) {
               Files.createDirectories(produitFolder);
              // LOGGER.info(DIRECTORY_CREATED + produitFolder);
           }
           Files.deleteIfExists(Paths.get(produitFolder + image.getImageUrl() + DOT + JPG_EXTENSION));
           Files.copy(file.getInputStream(), produitFolder.resolve(produitFolder.getFileName() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
           image.setImageUrl(setImageUrl(file.getOriginalFilename()));
           fileStorageService.save(file,"users");
           imageRepository.save(image);
          // LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + photoPrincipale.getOriginalFilename());

       }

   }

   public String setImageUrl(String nom) {
       return ServletUriComponentsBuilder.fromCurrentContextPath().path(PRODUIT_PATH + FORWARD_SLASH
               + nom ).toUriString();
   }
   
   
   public Image saveImage(Image image) {

       return imageRepository.save(image);
   }
}