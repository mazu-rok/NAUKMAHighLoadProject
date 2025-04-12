package ua.edu.ukma.events.services;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;

@Service
public class MinioStorageService implements StorageService {
    private final MinioClient minioClient;

    public MinioStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void uploadFile(String bucketName, String fileName, MultipartFile file) {
        try {
            boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isBucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .contentType(file.getContentType())
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());
        } catch (MinioException | java.io.IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    @Override
    public InputStream downloadFile(String bucketName, String fileName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (MinioException | java.io.IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error downloading file from MinIO", e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (MinioException | java.io.IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error deleting file from MinIO", e);
        }
    }
}
