package com.ddockddack.domain.similarity.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EnsembleModel {

    private final PerceptualHash perceptualHash;
    private final FeatureDetectorDescriptor featureDetectorDescriptor;
    private final StructuralSimilarity structuralSimilarity;
    private final ImageHistogram imageHistogram;

    private static double baseLog(double x, double base) {
        return Math.log10(x) / Math.log10(base);
    }

    /**
     * Hashing(Grayscale) + FeatureExtraction(KAZE) + StructuralSimilarity(SSIM, Grayscale) +
     * log{Histogram(RGB)} InputStream once ended, cannot reuse. Image data input format from
     * frontend: ByteArray final similarity score estimated 500±500
     */
    public Integer CalculateSimilarity(byte[] byteArray1, byte[] byteArray2) throws Exception {

        CompletableFuture<List<Mat>> histCompletableFuture1 = imageHistogram.getHistogram(
            ImageUtil.ByteArray2InputStream(byteArray1));
        CompletableFuture<List<Mat>> histCompletableFuture2 = imageHistogram.getHistogram(
            ImageUtil.ByteArray2InputStream(byteArray2));

        int result;

//      [+] *8192
        CompletableFuture<Double> compareFeaturesCompletableFuture = featureDetectorDescriptor.compareFeatures(
            ImageUtil.ByteArray2InputStream(byteArray1),
            ImageUtil.ByteArray2InputStream(byteArray2));

//      [+] *512

        CompletableFuture<Double> compareImagesCompletableFuture = structuralSimilarity.compareImages(
            ImageUtil.ByteArray2InputStream(byteArray1),
            ImageUtil.ByteArray2InputStream(byteArray2));

//      [-] *2
        CompletableFuture<String> hashCompletableFutures1 = perceptualHash.getHash(
            ImageUtil.ByteArray2InputStream(byteArray1));
        CompletableFuture<String> hashCompletableFutures2 = perceptualHash.getHash(
            ImageUtil.ByteArray2InputStream(byteArray1));
//      [-] *2, if NaN batch -32.0
        double histogramDiff = baseLog(
            imageHistogram.compareHistograms(histCompletableFuture1.get(),
                histCompletableFuture2.get()),
            2);
        if (Double.isNaN(histogramDiff)) {
            histogramDiff = 1.0;
        }

        double structureScore = compareImagesCompletableFuture.get();
        double featureScore = compareFeaturesCompletableFuture.get();

        double hashDistance = (perceptualHash.distance(hashCompletableFutures1.get(), hashCompletableFutures2.get()));
        result = (int) Math.round(
            (10000 * featureScore) + (500 * structureScore) - (hashDistance) - (4 * histogramDiff));

        return result;
    }

}
