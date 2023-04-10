package com.ddockddack.domain.similarity.service;

import lombok.RequiredArgsConstructor;
import nu.pattern.OpenCV;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.KAZE;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Configuration
@RequiredArgsConstructor
public class FeatureDetectorDescriptor {

    static {
        // Load the OpenCV library
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadLocally();

    }


    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Double> compareFeatures(InputStream input1, InputStream input2) throws IOException {

        BufferedImage img1 = ImageIO.read(input1);
        Mat image1 = ImageUtil.BufferedImage2Mat(img1);

        BufferedImage img2 = ImageIO.read(input2);
        Mat image2 = ImageUtil.BufferedImage2Mat(img2);

        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        // Detect keypoints and compute descriptors using KAZE
        KAZE detector = KAZE.create();
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            detector.detectAndCompute(image1, new Mat(), keypoints1, descriptors1);
        });

        CompletableFuture<Void> voidCompletableFuture2 = CompletableFuture.runAsync(() -> {
            detector.detectAndCompute(image2, new Mat(), keypoints2, descriptors2);
        });

        // Match descriptors using Brute-Force matcher
        List<MatOfDMatch> matches = new LinkedList<>();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_L1);
        CompletableFuture.allOf(voidCompletableFuture, voidCompletableFuture2).join();

        matcher.knnMatch(descriptors1, descriptors2, matches, 2);

        // Filter matches using the Lowe's ratio test = 0.75f
//        double ratioThreshold = 0.75f;
//        double ratioThreshold = 0.70f;
        double ratioThreshold = 0.80f;
//        double ratioThreshold = 0.90f;
//        double ratioThreshold = 0.999f;
        List<DMatch> goodMatches = new LinkedList<>();

        for (MatOfDMatch matOfDMatch : matches) {
            DMatch[] DMatchArray = matOfDMatch.toArray();

//          ERROR Handling: image does NOT have a feature point (ex. black-screen)
            if (DMatchArray.length != 0) {
                DMatch m1 = DMatchArray[0];
                DMatch m2 = DMatchArray[1];

                if (m1.distance <= m2.distance * ratioThreshold) {
                    goodMatches.add(m1);
                }
            }

//            DMatch m1 = DMatchArray[0];
//            DMatch m2 = DMatchArray[1];
//
//            if (m1.distance <= m2.distance * ratioThreshold) {
//                goodMatches.add(m1);
//            }
        }

        // Calculate similarity featureScore as the ratio of good matches to total matches
        double featureScore = (double) goodMatches.size() / (double) keypoints1.rows();
        return CompletableFuture.completedFuture(featureScore);
    }
}
