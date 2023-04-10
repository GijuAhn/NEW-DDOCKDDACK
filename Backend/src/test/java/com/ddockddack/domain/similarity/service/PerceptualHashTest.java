//package com.ddockddack.domain.similarity.service;
//
//import org.junit.jupiter.api.Test;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class PerceptualHashTest {
//
//    @Test
//    void distance() {
//    }
//
//    @Test
//    void getHash() throws Exception {
////        given
//        final PerceptualHash perceptualHash = new PerceptualHash();
//
//        File targetHandImage1 = new File("./src/test/resources/testImage/image_similarity_test/hand_target/hand1.jpg");
//        File targetHandImage2 = new File("./src/test/resources/testImage/image_similarity_test/hand_target/hand2.jpg");
//        File targetHandImage3 = new File("./src/test/resources/testImage/image_similarity_test/hand_target/hand3.jpg");
//        File targetHandImage4 = new File("./src/test/resources/testImage/image_similarity_test/hand_target/hand4.jpg");
//        File targetHandImage5 = new File("./src/test/resources/testImage/image_similarity_test/hand_target/hand5.jpg");
//        File targetHandImage6 = new File("./src/test/resources/testImage/image_similarity_test/hand_target/hand6.jpg");
//
//        InputStream targetHandImageStream1 = new FileInputStream(targetHandImage1);
//        InputStream targetHandImageStream2 = new FileInputStream(targetHandImage2);
//        InputStream targetHandImageStream3 = new FileInputStream(targetHandImage3);
//        InputStream targetHandImageStream4 = new FileInputStream(targetHandImage4);
//        InputStream targetHandImageStream5 = new FileInputStream(targetHandImage5);
//        InputStream targetHandImageStream6 = new FileInputStream(targetHandImage6);
//
//        File myHandImage1 = new File("./src/test/resources/testImage/image_similarity_test/hand_input/hand1/1.jpg");
//        File myHandImage2 = new File("./src/test/resources/testImage/image_similarity_test/hand_input/hand2/1.jpg");
//        File myHandImage3 = new File("./src/test/resources/testImage/image_similarity_test/hand_input/hand3/1.jpg");
//        File myHandImage4 = new File("./src/test/resources/testImage/image_similarity_test/hand_input/hand4/1.jpg");
//        File myHandImage5 = new File("./src/test/resources/testImage/image_similarity_test/hand_input/hand5/1.jpg");
//        File myHandImage6 = new File("./src/test/resources/testImage/image_similarity_test/hand_input/hand6/1.jpg");
//
//        InputStream myHandImageStream1 = new FileInputStream(myHandImage1);
//        InputStream myHandImageStream2 = new FileInputStream(myHandImage2);
//        InputStream myHandImageStream3 = new FileInputStream(myHandImage3);
//        InputStream myHandImageStream4 = new FileInputStream(myHandImage4);
//        InputStream myHandImageStream5 = new FileInputStream(myHandImage5);
//        InputStream myHandImageStream6 = new FileInputStream(myHandImage6);
//
////        when
//        final String targetHash1 = perceptualHash.getHash(targetHandImageStream1);
//        final String targetHash2 = perceptualHash.getHash(targetHandImageStream2);
//        final String targetHash3 = perceptualHash.getHash(targetHandImageStream3);
//        final String targetHash4 = perceptualHash.getHash(targetHandImageStream4);
//        final String targetHash5 = perceptualHash.getHash(targetHandImageStream5);
//        final String targetHash6 = perceptualHash.getHash(targetHandImageStream6);
//
//        final String inputHash1 = perceptualHash.getHash(myHandImageStream1);
//        final String inputHash2 = perceptualHash.getHash(myHandImageStream2);
//        final String inputHash3 = perceptualHash.getHash(myHandImageStream3);
//        final String inputHash4 = perceptualHash.getHash(myHandImageStream4);
//        final String inputHash5 = perceptualHash.getHash(myHandImageStream5);
//        final String inputHash6 = perceptualHash.getHash(myHandImageStream6);
////        then
////        System.out.println(perceptualHash.distance(targetHash, inputHash));
//        System.out.println("---------------------일치(점수가 낮아야 함)");
//        System.out.println(perceptualHash.distance(targetHash1, inputHash1));
//        System.out.println(perceptualHash.distance(targetHash2, inputHash2));
//        System.out.println(perceptualHash.distance(targetHash3, inputHash3));
//        System.out.println(perceptualHash.distance(targetHash4, inputHash4));
//        System.out.println(perceptualHash.distance(targetHash5, inputHash5));
//        System.out.println(perceptualHash.distance(targetHash6, inputHash6));
//        System.out.println("---------------------불일치(점수가 높아야 함)");
//        System.out.println(perceptualHash.distance(targetHash1, inputHash6));
//        System.out.println(perceptualHash.distance(targetHash2, inputHash5));
//        System.out.println(perceptualHash.distance(targetHash3, inputHash4));
//        System.out.println(perceptualHash.distance(targetHash4, inputHash3));
//        System.out.println(perceptualHash.distance(targetHash5, inputHash2));
//        System.out.println(perceptualHash.distance(targetHash6, inputHash1));
//
//    }
//}