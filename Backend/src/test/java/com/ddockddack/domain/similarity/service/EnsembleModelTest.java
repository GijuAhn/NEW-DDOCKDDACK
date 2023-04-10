//package com.ddockddack.domain.similarity.service;
//
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.nio.file.Files;
//
//class EnsembleModelTest {
//
//    @Test
//    void calculateSimilarity() throws Exception {
//
////        final PerceptualHash perceptualHash = new PerceptualHash();
////        final FeatureDetectorDescriptor featureDetectorDescriptor = new FeatureDetectorDescriptor();
////        final StructuralSimilarity structuralSimilarity = new StructuralSimilarity();
////        final ImageHistogram imageHistogram = new ImageHistogram();
//        final EnsembleModel ensembleModel = new EnsembleModel();
//
//        File hurray1 = new File("./src/test/resources/testImage/image_similarity_test/hurray1.jpg");
//        byte[] hurray1byteArray = Files.readAllBytes(hurray1.toPath());
//
//        File hurray2 = new File("./src/test/resources/testImage/image_similarity_test/hurray2.jpg");
//        byte[] hurray2byteArray = Files.readAllBytes(hurray2.toPath());
//
//        File hurray3 = new File("./src/test/resources/testImage/image_similarity_test/hurray3.jpg");
//        byte[] hurray3byteArray = Files.readAllBytes(hurray3.toPath());
//
//        File arms1 = new File("./src/test/resources/testImage/image_similarity_test/arms1.jpg");
//        byte[] arms1byteArray = Files.readAllBytes(arms1.toPath());
//
//        File arms2 = new File("./src/test/resources/testImage/image_similarity_test/arms2.jpg");
//        byte[] arms2byteArray = Files.readAllBytes(arms2.toPath());
//
//        File arms3 = new File("./src/test/resources/testImage/image_similarity_test/arms3.jpg");
//        byte[] arms3byteArray = Files.readAllBytes(arms3.toPath());
//
//        File face1 = new File("./src/test/resources/testImage/image_similarity_test/face1.jpg");
//        byte[] face1byteArray = Files.readAllBytes(face1.toPath());
//
//        File face2 = new File("./src/test/resources/testImage/image_similarity_test/face2.jpg");
//        byte[] face2byteArray = Files.readAllBytes(face2.toPath());
//
//        File face3 = new File("./src/test/resources/testImage/image_similarity_test/face3.jpg");
//        byte[] face3byteArray = Files.readAllBytes(face3.toPath());
//
//        File face4 = new File("./src/test/resources/testImage/image_similarity_test/face4.jpeg");
//        byte[] face4byteArray = Files.readAllBytes(face4.toPath());
//
//
////        System.out.println(EnsembleModel.CalculateSimilarity(hurray1byteArray, hurray2byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(hurray1byteArray, hurray3byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(hurray2byteArray, hurray3byteArray));
////        System.out.println("---------------------------------------");
////        System.out.println(EnsembleModel.CalculateSimilarity(arms1byteArray, arms2byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(arms1byteArray, arms3byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(arms2byteArray, arms3byteArray));
////        System.out.println("---------------------------------------");
////        System.out.println(EnsembleModel.CalculateSimilarity(face1byteArray, face2byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(face1byteArray, face3byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(face1byteArray, face4byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(face2byteArray, face3byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(face2byteArray, face4byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(face3byteArray, face4byteArray));
//    }
//}