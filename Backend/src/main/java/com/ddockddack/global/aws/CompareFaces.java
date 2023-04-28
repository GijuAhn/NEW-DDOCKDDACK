package com.ddockddack.global.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.Image;
import java.nio.ByteBuffer;
import javax.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class CompareFaces {

    static Float THRESHOLD = 0F;
    @Value("${cloud.aws.rekognition.access-key}")
    private String accessKey;

    @Value("${cloud.aws.rekognition.secret-key}")
    private String secretKey;

    private AmazonRekognition rekognitionClient;

    @PostConstruct
    public void setRekognitionClient() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        rekognitionClient = AmazonRekognitionClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.AP_NORTHEAST_2)
            .build();
    }

    public Float compare(byte[] target, byte[] source) {
        Image sourceImage = new Image()
            .withBytes(ByteBuffer.wrap(source));
        Image targetImage = new Image()
            .withBytes(ByteBuffer.wrap(target));

        CompareFacesRequest request = new CompareFacesRequest()
            .withSourceImage(sourceImage)
            .withTargetImage(targetImage)
            .withSimilarityThreshold(THRESHOLD);

        CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);

        return compareFacesResult.getFaceMatches().get(0).getSimilarity();
    }
}