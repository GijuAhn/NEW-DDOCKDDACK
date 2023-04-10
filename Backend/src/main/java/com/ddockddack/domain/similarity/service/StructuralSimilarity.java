package com.ddockddack.domain.similarity.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;

import com.ddockddack.domain.similarity.service.ImageUtil;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.ImageExtensionException;
import org.opencv.core.Mat;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

// grayscale (single channel) SSIM
@Configuration
public class StructuralSimilarity {
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Double> compareImages(InputStream image1, InputStream image2) throws IOException {
        BufferedImage rawImg1 = toGrayscale(ImageIO.read(image1));
        BufferedImage rawImg2 = toGrayscale(ImageIO.read(image2));

        int width = Math.min(rawImg1.getWidth(), rawImg2.getWidth());
        int height = Math.min(rawImg1.getHeight(), rawImg2.getHeight());

        BufferedImage img1 = ImageUtil.Resizer.PROGRESSIVE_BILINEAR.resize(rawImg1, width, height);
        BufferedImage img2 = ImageUtil.Resizer.PROGRESSIVE_BILINEAR.resize(rawImg2, width, height);

        double meanX = 0;
        double meanY = 0;
        double standardDeviationX = 0;
        double standardDeviationY = 0;
        double covariance = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int gray1 = img1.getRGB(x, y) & 0xff;
                int gray2 = img2.getRGB(x, y) & 0xff;

                meanX += gray1;
                meanY += gray2;
            }
        }

        meanX /= (width * height);
        meanY /= (width * height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int gray1 = img1.getRGB(x, y) & 0xff;
                int gray2 = img2.getRGB(x, y) & 0xff;

                standardDeviationX += Math.pow(gray1 - meanX, 2);
                standardDeviationY += Math.pow(gray2 - meanY, 2);
                covariance += (gray1 - meanX) * (gray2 - meanY);
            }
        }

        standardDeviationX = Math.sqrt(standardDeviationX / (width * height));
        standardDeviationY = Math.sqrt(standardDeviationY / (width * height));
        covariance /= (width * height);

        double SSIMScore = (2 * meanX * meanY + 0.0001) * (2 * covariance + 0.0001) /
                ((meanX * meanX + meanY * meanY + 0.0001) * (standardDeviationX * standardDeviationX + standardDeviationY * standardDeviationY + 0.0001));

//        prevent SSIMScore got negative value, logarithm shift
//        [-1.0 ~ +1.0 -> -1.0 ~ +1.0] -> [-1.0 ~ +1.0 -> +0.0 ~ +ln3]
        double shiftedSSIMScore = logShift(SSIMScore);
//        System.out.println("@ShiftedSSIM = " + shiftedSSIMScore);

        //        HIGHER SSIM => HIGHER Similarity
        return CompletableFuture.completedFuture(shiftedSSIMScore);
    }

    private static BufferedImage toGrayscale(BufferedImage image) {
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        grayImage.getGraphics().drawImage(image, 0, 0, null);
        return grayImage;
    }

//  prevent SSIMScore got negative value, logarithm shift
//  [-1.0 ~ +1.0 -> -1.0 ~ +1.0] -> [-1.0 ~ +1.0 -> +0.0 ~ +ln3]
    private double logShift(double x){
        double y;
        y = Math.log(x+2);
        return y;
    }
}
