package com.ddockddack.domain.similarity.service;

import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

@Configuration
@RequiredArgsConstructor
public class EnsembleModel {

    private final PerceptualHash perceptualHash;
    private final FeatureDetectorDescriptor featureDetectorDescriptor;
    private final StructuralSimilarity structuralSimilarity;
    private final ImageHistogram imageHistogram;

    //    Hashing(Grayscale) + FeatureExtraction(KAZE) + StructuralSimilarity(SSIM, Grayscale) + log{Histogram(RGB)}
//    InputStream once ended, cannot reuse.
//    Image data input format from frontend: ByteArray
//    public static double CalculateSimilarity(InputStream inputStream1, InputStream inputStream2) throws Exception {
//    @Async("threadPoolTaskExecutor")
    public Integer CalculateSimilarity(byte[] byteArray1, byte[] byteArray2) throws Exception {

//        byte[] byteArray1 = ImageUtil.InputStream2ByteArray(inputStream1);
//        byte[] byteArray2 = ImageUtil.InputStream2ByteArray(inputStream2);
        CompletableFuture<List<Mat>> hist1 = imageHistogram.getHistogram(ImageUtil.ByteArray2InputStream(byteArray1));
        CompletableFuture<List<Mat>> hist2 = imageHistogram.getHistogram(ImageUtil.ByteArray2InputStream(byteArray2));

        int result;

//        result = (double) (
//                8192 * (featureDetectorDescriptor.compareFeatures(
//                        ImageUtil.ByteArray2InputStream(byteArray1),
//                        ImageUtil.ByteArray2InputStream(byteArray2)))
//
//                + 512 * (structuralSimilarity.compareImages(
//                ImageUtil.ByteArray2InputStream(byteArray1),
//                ImageUtil.ByteArray2InputStream(byteArray2)))
//
//                - 2 * (perceptualHash.distance(
//                perceptualHash.getHash(ImageUtil.ByteArray2InputStream(byteArray1)),
//                perceptualHash.getHash(ImageUtil.ByteArray2InputStream(byteArray2))))
//
//                - 2 * baseLog(imageHistogram.compareHistograms(hist1, hist2),2)
//        );

//      [+] *8192
        CompletableFuture<Double> doubleCompletableFuture = featureDetectorDescriptor.compareFeatures(
                ImageUtil.ByteArray2InputStream(byteArray1),
                ImageUtil.ByteArray2InputStream(byteArray2));

//      [+] *512

        CompletableFuture<Double> doubleCompletableFuture1 = structuralSimilarity.compareImages(
                ImageUtil.ByteArray2InputStream(byteArray1),
                ImageUtil.ByteArray2InputStream(byteArray2));

//      [-] *2
        CompletableFuture<String> hash1 = perceptualHash.getHash(ImageUtil.ByteArray2InputStream(byteArray1));
        CompletableFuture<String> hash2 = perceptualHash.getHash(ImageUtil.ByteArray2InputStream(byteArray1));
//      [-] *2, if NaN batch -32.0
        double histogramDiff = (double) baseLog(imageHistogram.compareHistograms(hist1.get(), hist2.get()), 2);
        if (Double.isNaN(histogramDiff)) {
            histogramDiff = 1.0;
        }
//        System.out.println("[+] featureScore * 8192: " + (8192 * featureScore));
//        System.out.println("[+] structureScore * 512: " + (512 * structureScore));
//        System.out.println("[-] hashDistance * 2: " + (2 * hashDistance));
//        System.out.println("[-] histogramDiff * 2: " + (2 * histogramDiff));

//        result = (int) Math.round((8192 * featureScore) + (512 * structureScore) - (2 * hashDistance) - (2 * histogramDiff));
//        adjust(ease) weight cause of relative evaluation
        double structureScore = doubleCompletableFuture1.get();
        double featureScore = doubleCompletableFuture.get();

        double hashDistance = (perceptualHash.distance(hash1.get(),hash2.get()));
        result = (int) Math.round((10000 * featureScore) + (500 * structureScore) - (hashDistance) - (4 * histogramDiff));

//        final similarity score
//        estimated 500±500
        return result;
    }

    private static double baseLog(double x, double base) {
        return Math.log10(x) / Math.log10(base);
    }

}
