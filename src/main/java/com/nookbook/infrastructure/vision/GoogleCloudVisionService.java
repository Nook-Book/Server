package com.nookbook.infrastructure.vision;

import com.google.cloud.vision.v1.*;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GoogleCloudVisionService {

    @Value("${spring.cloud.gcp.storage.bucket.filePath}")
    String bucketFilePath;

    private final GoogleStorageUploader googleStorageUploader;

    public ResponseEntity<?> detectTextGcs(MultipartFile file) {
        googleStorageUploader.upload(file);
        String filePath = bucketFilePath + file.getOriginalFilename();

        return detectTextGcs(filePath);
    }

    // Detects text in the specified remote image on Google Cloud Storage.
    public ResponseEntity<?> detectTextGcs(String gcsPath) {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.

        // Google Cloud Vision API 클라이언트 초기화
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            // 이미지 감지 요청 전송
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            for (AnnotateImageResponse res : response.getResponsesList()) {
                if (res.hasError()) {
                    System.err.println("Error: " + res.getError().getMessage());
                    return ResponseEntity.badRequest().body("Error processing image");
                }

                // 감지된 텍스트 추출
                String detectedText = res.getFullTextAnnotation().getText();
                detectedText = detectedText.replace("\n", " "); // 줄바꿈 문자를 공백으로 대체

                // 추출 완료 후 파일 삭제
                googleStorageUploader.delete(gcsPath);

                ApiResponse apiResponse = ApiResponse.builder()
                        .check(true)
                        .information(detectedText)
                        .build();
                return ResponseEntity.ok(apiResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error");
        } finally {
            googleStorageUploader.delete(gcsPath); // 항상 파일 삭제
        }
        return ResponseEntity.ok().build();
    }
}