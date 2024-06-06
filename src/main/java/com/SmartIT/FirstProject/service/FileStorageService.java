package com.SmartIT.FirstProject.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class FileStorageService {

    @Value("${filestoragePath}")
    public String filestoragePath;

    public final Map<String, Path> directories = new HashMap<>();

    @PostConstruct
    public void init() {
       
        directories.put("users", Paths.get(filestoragePath, "users"));

        directories.forEach((key, path) -> {
            try {
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not initialize folder for " + key, e);
            }
        });
    }

    public Path getUploadDirPath() {
        return Paths.get(filestoragePath).toAbsolutePath().normalize();
    }
    public Path getPathForType(String fileType) {
        Path path = directories.get(fileType);
        if (path == null) {
            throw new IllegalArgumentException("Invalid file type specified");
        }
        return path;
    }

    public String save(MultipartFile file, String folder) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new NoSuchElementException("Invalid filename: " + fileName);
            }

            Path targetLocation = getPathForType(folder).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), ex);
        }
    }

    public String saveCompressed(Path sourcePath, String folder, String fileName) {
        try {
            if (fileName.contains("..")) {
                throw new SecurityException("Invalid path sequence: " + fileName);
            }

            Path targetLocation = getPathForType(folder).resolve(fileName);
            Files.copy(sourcePath, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName, ex);
        }
    }


    public String saveCode(File file, String folder) {
        try {
            String fileName = file.getName();
            if (fileName.contains("..")) {
                throw new NoSuchElementException("Invalid filename: " + fileName);
            }

            Path targetLocation = getPathForType(folder).resolve(fileName);
            Files.copy(file.toPath(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getName(), ex);
        }
    }

    public boolean deleteFileByName(String name, String folder) {
        Path file = getPathForType(folder).resolve(name);
        try {
            if (Files.exists(file)) {
                Files.delete(file);
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException("Could not delete the file: " + name, e);
        }
    }

   


    public Resource load(String filename, String folder) {
//        try {
//            Path directoryPath = getPathForType(folder);
//            Path file = directoryPath.resolve(filename);
//            Resource resource = new UrlResource(file.toUri());
//
//            if (resource.exists() || resource.isReadable()) {
//                return resource;
//            } else {
//                return null;
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("Error: " + e.getMessage());
//        }
//    }

        try {
            Path root = Paths.get("uploads/" + folder);
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            System.out.println(filename);

            if (resource.exists() || resource.isReadable()) {
                System.out.println(resource);
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }


    }
}