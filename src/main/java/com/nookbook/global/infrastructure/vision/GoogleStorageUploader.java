package com.nookbook.global.infrastructure.vision;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class GoogleStorageUploader {

    @Value("${spring.cloud.gcp.storage.bucket.name}")
    String bucketName;

    private static final Storage storage = StorageOptions.getDefaultInstance().getService();

    public String upload(MultipartFile file) {
        try {
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder(bucketName, file.getOriginalFilename()).build(),
                    file.getBytes()
            );
            return blobInfo.getMediaLink();
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String gcsPath) {
        // gcsPath에서 버킷 이름과 객체 이름을 추출
        String[] parts = gcsPath.split("/", 4);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid GCS path format");
        }
        String bucketName = parts[2];
        String objectName = parts[3];

        BlobId blobId = BlobId.of(bucketName, objectName);

        boolean deleted = storage.delete(blobId);
        if (deleted) {
            System.out.println("File deleted successfully: " + gcsPath);
        } else {
            System.out.println("Failed to delete file: " + gcsPath);
        }
    }

}