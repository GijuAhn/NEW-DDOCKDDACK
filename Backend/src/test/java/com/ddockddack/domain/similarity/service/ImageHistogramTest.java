//package com.ddockddack.domain.similarity.service;
//
//import org.junit.jupiter.api.Test;
//import org.opencv.core.Mat;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//
//class ImageHistogramTest {
//
//    @Test
//    void getHistogram() throws IOException {
//
//        final ImageHistogram imageHistogram = new ImageHistogram();
//        /*
//        * Inputstream 이 아닌, image 를 입력으로 사용할 경우
//        * image 는 반드시 JPG 포맷이어야 함!!!
//        * PNG 포맷을 input 으로 넣으면
//        * !buf.empty() in function 'cv::imdecode_'
//        * 에러발생
//        * */
//
////        calculate histogram similarity
//
//        File myHandImage0 = new File("./src/test/resources/testImage/image_similarity_test/hand_input/hand1/3.jpg");
//        List<Mat> hist1;
//        try (InputStream targetHandImageStream1 = new FileInputStream(myHandImage0)) {
//            hist1 = imageHistogram.getHistogram(targetHandImageStream1);
//            System.out.println("hist1 = " + hist1);
//        }
//
//        File myHandImage1 = new File("./src/test/resources/testImage/image_similarity_test/hand_input/hand1/1.jpg");
//        List<Mat> hist2;
//        try (InputStream myHandImageStream1 = new FileInputStream(myHandImage1)) {
//            hist2 = imageHistogram.getHistogram(myHandImageStream1);
//            System.out.println("hist2 = " + hist2);
//        }
//
//        File myHandImage2 = new File("./src/test/resources/testImage/image_similarity_test/hand_input/hand2/1.jpg");
//        List<Mat> hist3;
//        try (InputStream myHandImageStream2 = new FileInputStream(myHandImage2)) {
//            hist3 = imageHistogram.getHistogram(myHandImageStream2);
//            System.out.println("hist3 = " + hist3);
//        }
//
////        print comparison result
//        System.out.println("hist 1vs2 = " + imageHistogram.compareHistograms(hist1, hist2));
//        System.out.println("hist 1vs3 = " + imageHistogram.compareHistograms(hist1, hist3));
//
//    }
//}