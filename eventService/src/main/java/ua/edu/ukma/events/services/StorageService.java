package ua.edu.ukma.events.services;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void uploadFile(String bucketName, String fileName, MultipartFile file);

    InputStream downloadFile(String bucketName, String fileName);

    void deleteFile(String bucketName, String fileName);
}
