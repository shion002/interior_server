package port.interior.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.credentials.access-key}")
    private String accessKey;

    @Value("${aws.credentials.secret-key}")
    private String secretKey;

    private S3Presigner createPresigner(){
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String generatePresignedUrl(Long postId, String fileName) {
        S3Presigner presigner = createPresigner();

        String objectKey = "interior/posts/" + postId + "/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        log.info("putObjRequest={}", putObjectRequest);
        Instant now = Instant.now();
        log.info("Current UTC time: {}", now);

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest));

        return presignedRequest.url().toString(); // 업로드 URL 반환
    }


    public void deleteFile(String fileUrl) {
        log.info("Received fileUrl: {}", fileUrl);

        String expectedPrefix = "https://shion002.s3.ap-southeast-2.amazonaws.com/";
        log.info("Expected Prefix: {}", expectedPrefix);

        if (!fileUrl.startsWith(expectedPrefix)) {
            log.error("Invalid fileUrl format: {}", fileUrl);
            throw new IllegalArgumentException("Invalid fileUrl format: " + fileUrl);
        }

        String objectKey = fileUrl.replace(expectedPrefix, "");
        log.info("Extracted objectKey: {}", objectKey);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            log.info("Deleting object: {}", objectKey);
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted object: {}", objectKey);
        } catch (Exception e) {
            log.error("Failed to delete object: {}", objectKey, e);
            throw new RuntimeException("S3에서 파일 삭제 실패: " + fileUrl, e);
        }
    }
}




















