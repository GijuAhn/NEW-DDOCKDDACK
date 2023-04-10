package com.ddockddack.domain.similarity.service;

import com.ddockddack.global.error.ErrorCode;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.ddockddack.domain.similarity.service.ImageUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
@Configuration
public class ImageHistogram {
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<Mat>> getHistogram(InputStream is) throws IOException {
        OpenCV.loadLocally();

        BufferedImage img = ImageIO.read(is);
        Mat image = ImageUtil.BufferedImage2Mat(img);

        List<Mat> histogram = null;

        try {
            // Histogram Start
            List<Mat> bgrPlanes = new ArrayList<>();
            Core.split(image, bgrPlanes);

            int histSize = 256;
            boolean accumulate = false;

            float[] range = {0, 256};
            MatOfFloat histRange = new MatOfFloat(range);
            Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
            Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(),
                    bHist, new MatOfInt(histSize), histRange, accumulate);
            Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(),
                    gHist, new MatOfInt(histSize), histRange, accumulate);
            Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(),
                    rHist, new MatOfInt(histSize), histRange, accumulate);

            int histW = 512, histH = 400;
            int binW = (int) Math.round((double) histW / histSize);

            Mat imageHist = new Mat(histH, histW, CvType.CV_8UC3, new Scalar(0, 0, 0));

            Core.normalize(bHist, bHist, 0, imageHist.rows(), Core.NORM_MINMAX);
            Core.normalize(gHist, gHist, 0, imageHist.rows(), Core.NORM_MINMAX);
            Core.normalize(rHist, rHist, 0, imageHist.rows(), Core.NORM_MINMAX);

            float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
            bHist.get(0, 0, bHistData);
            float[] gHistData = new float[(int) (gHist.total() * gHist.channels())];
            gHist.get(0, 0, gHistData);
            float[] rHistData = new float[(int) (rHist.total() * rHist.channels())];
            rHist.get(0, 0, rHistData);

            for (int i = 1; i < histSize; i++) {
                Imgproc.line(imageHist,
                        new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
                        new Point(binW * (i), histH - Math.round(bHistData[i])),
                        new Scalar(255, 0, 0), 1);
                Imgproc.line(imageHist,
                        new Point(binW * (i - 1), histH - Math.round(gHistData[i - 1])),
                        new Point(binW * (i), histH - Math.round(gHistData[i])),
                        new Scalar(0, 255, 0), 1);
                Imgproc.line(imageHist,
                        new Point(binW * (i - 1), histH - Math.round(rHistData[i - 1])),
                        new Point(binW * (i), histH - Math.round(rHistData[i])),
                        new Scalar(0, 0, 255), 1);
            }
            // Histogram End
            // Showing Histogram code [removed]

            // return histogram data for calculate histogram similarity
            histogram = new ArrayList<>();

            histogram.add(bHist);
            histogram.add(gHist);
            histogram.add(rHist);


        } catch (CvException e) {
            histogram = null;
        } finally {
            return CompletableFuture.completedFuture(histogram);
        }
    }

    //    compare histogram similarity
//    LOWER Comparison value (distance) == HIGHER Similarity
    public double compareHistograms(List<Mat> hist1, List<Mat> hist2) {
        if (hist1 == null || hist2 == null) {
            return 1.0;
        } else {
            double result = 0.0;
            result = Imgproc.compareHist(hist1.get(0), hist2.get(0), Imgproc.CV_COMP_CHISQR);
            result += Imgproc.compareHist(hist1.get(1), hist2.get(1), Imgproc.CV_COMP_CHISQR);
            result += Imgproc.compareHist(hist1.get(2), hist2.get(2), Imgproc.CV_COMP_CHISQR);
//            System.out.println("Result of histogram comparison: "+ result);
            return result;
        }
    }
}

