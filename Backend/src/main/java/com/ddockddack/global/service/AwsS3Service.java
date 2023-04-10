package com.ddockddack.global.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@NoArgsConstructor
public class AwsS3Service {

    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.access-key}")
    private String accesskey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accesskey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    public String multipartFileUpload(MultipartFile uploadFile) {
        String fileName = UUID.randomUUID().toString() + uploadFile.getOriginalFilename();
        try (InputStream inputStream = uploadFile.getInputStream()){
            ObjectMetadata om = new ObjectMetadata();
            om.setContentType(uploadFile.getContentType());
            byte[] bytes = uploadFile.getBytes();
            om.setContentLength(bytes.length);
            s3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, om).withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public String InputStreamUpload(byte[] byteImages) {
        ObjectMetadata om = new ObjectMetadata();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteImages);
        om.setContentType("image/jpg");
        om.setContentLength(byteImages.length);
        String fileName = UUID.randomUUID().toString() + ".jpg";
        s3Client.putObject(new PutObjectRequest(bucket, fileName, byteArrayInputStream, om).withCannedAcl(CannedAccessControlList.PublicRead));
        return fileName;
    }

    public byte[] getObject(String fileName) {
        S3Object object = s3Client.getObject(new GetObjectRequest(bucket, fileName));
        S3ObjectInputStream objectContent = object.getObjectContent();
        byte[] bytes = new byte[0];
        try {
            bytes = IOUtils.toByteArray(objectContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public void deleteObject(String fileName) {
        s3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }


}
