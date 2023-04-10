//package com.ddockddack.domain.similarity.service;
//
//import org.junit.jupiter.api.Test;
//import org.opencv.core.Mat;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class PerceptualHashTest2 {
//
//    @Test
//    void distance() {
//    }
//
//    @Test
//    void getHash() throws Exception {
//        final PerceptualHash perceptualHash = new PerceptualHash();
//
//        File hurray1 = new File("./src/test/resources/testImage/image_similarity_test/hurray1.jpg");
//        InputStream hurrayStream1 = new FileInputStream(hurray1);
//
//        File hurray2 = new File("./src/test/resources/testImage/image_similarity_test/hurray2.jpg");
//        InputStream hurrayStream2 = new FileInputStream(hurray2);
//
//        File hurray3 = new File("./src/test/resources/testImage/image_similarity_test/hurray3.jpg");
//        InputStream hurrayStream3 = new FileInputStream(hurray3);
//
//
//        File arms1 = new File("./src/test/resources/testImage/image_similarity_test/arms1.jpg");
//        InputStream armsStream1 = new FileInputStream(arms1);
//
//        File arms2 = new File("./src/test/resources/testImage/image_similarity_test/arms2.jpg");
//        InputStream armsStream2 = new FileInputStream(arms2);
//
//        File arms3 = new File("./src/test/resources/testImage/image_similarity_test/arms3.jpg");
//        InputStream armsStream3 = new FileInputStream(arms3);
//
//
//        File face1 = new File("./src/test/resources/testImage/image_similarity_test/face1.jpg");
//        InputStream faceStream1 = new FileInputStream(face1);
//
//        File face2 = new File("./src/test/resources/testImage/image_similarity_test/face2.jpg");
//        InputStream faceStream2 = new FileInputStream(face2);
//
//        File face3 = new File("./src/test/resources/testImage/image_similarity_test/face3.jpg");
//        InputStream faceStream3 = new FileInputStream(face3);
//
//        File face4 = new File("./src/test/resources/testImage/image_similarity_test/face4.jpeg");
//        InputStream faceStream4 = new FileInputStream(face4);
//
//
//
////        InputStream cannot be reusable (if stream reaches to the end, destroyed and cannot revert)
//        System.out.println("같은 동작 비교");
//        System.out.println("hurray 1,2: " +
//                perceptualHash.distance(
//                    perceptualHash.getHash(hurrayStream1),
//                    perceptualHash.getHash(hurrayStream2)
//                ));
//        System.out.println("arms 1,2: " +
//                perceptualHash.distance(
//                    perceptualHash.getHash(armsStream1),
//                    perceptualHash.getHash(armsStream2)
//                ));
//
//        System.out.println("-------------------------");
//        System.out.println("다른 동작 비교");
//        System.out.println("hurray vs arms: " +
//                perceptualHash.distance(
//                        perceptualHash.getHash(hurrayStream3),
//                        perceptualHash.getHash(armsStream3)
//                ));
//        System.out.println("-------------------------");
//        System.out.println("비슷한 오브젝트, 다른 도메인 비교");
//        System.out.println("face(사람얼굴,사람얼굴그림) 1,2: " +
//                perceptualHash.distance(
//                    perceptualHash.getHash(faceStream1),
//                    perceptualHash.getHash(faceStream2)
//                ));
//        System.out.println("face(호랑이얼굴,화난얼굴) 3,4: " +
//                perceptualHash.distance(
//                        perceptualHash.getHash(faceStream3),
//                        perceptualHash.getHash(faceStream4)
//                ));
//    }
//}