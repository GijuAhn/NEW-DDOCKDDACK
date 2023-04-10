package com.ddockddack.domain.similarity.service;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.InputStream;


// OpenCV package

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;


import javax.imageio.ImageIO;

@Configuration
public class PerceptualHash {

//    private int size = 32;
    private int size = 32;
//    private int smallerSize = 8;
    private int smallerSize = 16;

    public PerceptualHash() {
        initCoefficients();
    }

    public PerceptualHash(int size, int smallerSize) {
        this.size = size;
        this.smallerSize = smallerSize;

        initCoefficients();
    }

//    LOWER Hash Distance => HIGHER Similarity

    public int distance(String s1, String s2) {
        int counter = 0;
        for (int k = 0; k < s1.length(); k++) {
            if (s1.charAt(k) != s2.charAt(k)) {
                counter++;
            }
        }
        return counter;
    }

    private BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

    private BufferedImage grayscale(BufferedImage img) {
        colorConvert.filter(img, img);
        return img;
    }

    private static int getBlue(BufferedImage img, int x, int y) {
        return (img.getRGB(x, y)) & 0xff;
    }

    private double[] c;

    private void initCoefficients() {
        c = new double[size];

        for (int i = 1; i < size; i++) {
            c[i] = 1;
        }
        c[0] = 1 / Math.sqrt(2.0);
    }
    public double[][] applyDCT(double[][] f) {
        int N = size;

        double[][] F = new double[N][N];
        for (int u = 0; u < N; u++) {
            for (int v = 0; v < N; v++) {
                double sum = 0.0;
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        sum += Math.cos(((2 * i + 1) / (2.0 * N)) * u * Math.PI) * Math.cos(((2 * j + 1) / (2.0 * N)) * v * Math.PI) * (f[i][j]);
                    }
                }
                sum *= ((c[u] * c[v]) / 4.0);
                F[u][v] = sum;
            }
        }
        return F;
    }
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String> getHash(InputStream is) throws Exception {

        // Returns a 'binary string' (like. 001010111011100010) which is easy to do a hamming distance on.
        BufferedImage img = ImageIO.read(is);

        /* 1. Reduce size.
         * Like Average Hash, pHash starts with a small image.
         * However, the image is larger than 8x8; 32x32 is a good size.
         * This is really done to simplify the DCT computation and not
         * because it is needed to reduce the high frequencies.
         */
        img = resize(img, size, size);

        /* 2. Reduce color.
         * The image is reduced to a grayscale just to further simplify
         * the number of computations.
         */
        img = grayscale(img);

        double[][] vals = new double[size][size];

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                vals[x][y] = getBlue(img, x, y);
            }
        }

        /* 3. Compute the DCT.
         * The DCT separates the image into a collection of frequencies
         * and scalars. While JPEG uses an 8x8 DCT, this algorithm uses
         * a 32x32 DCT.
         */
        long start = System.currentTimeMillis();
        double[][] dctVals = applyDCT(vals);
//        System.out.println("DCT: " + (System.currentTimeMillis() - start));

        /* 4. Reduce the DCT.
         * This is the magic step. While the DCT is 32x32, just keep the
         * top-left 8x8. Those represent the lowest frequencies in the
         * picture.
         */
        /* 5. Compute the average value.
         * Like the Average Hash, compute the mean DCT value (using only
         * the 8x8 DCT low-frequency values and excluding the first term
         * since the DC coefficient can be significantly different from
         * the other values and will throw off the average).
         */
        double total = 0;

        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                total += dctVals[x][y];
            }
        }
        total -= dctVals[0][0];

        double avg = total / (double) ((smallerSize * smallerSize) - 1);

        /* 6. Further reduce the DCT.
         * This is the magic step. Set the 64 hash bits to 0 or 1
         * depending on whether each of the 64 DCT values is above or
         * below the average value. The result doesn't tell us the
         * actual low frequencies; it just tells us the very-rough
         * relative scale of the frequencies to the mean. The result
         * will not vary as long as the overall structure of the image
         * remains the same; this can survive gamma and color histogram
         * adjustments without a problem.
         */
        StringBuilder hash = new StringBuilder();

        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                if (x != 0 && y != 0) {
                    hash.append(dctVals[x][y] > avg ? "1" : "0");
                }
            }
        }
        return CompletableFuture.completedFuture(hash.toString());
    }

}