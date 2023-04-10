//package com.ddockddack.domain.similarity.service;
//
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.nio.file.Files;
//
//public class EnsembleModelTest2 {
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
//        File gunny = new File("./src/test/resources/testImage/image_similarity_test/gunny.jpg");
//        byte[] gunnybyteArray = Files.readAllBytes(gunny.toPath());
//
//        File granny = new File("./src/test/resources/testImage/image_similarity_test/granny.jpg");
//        byte[] grannybyteArray = Files.readAllBytes(granny.toPath());
//
//        File homin = new File("./src/test/resources/testImage/image_similarity_test/homin.jpg");
//        byte[] hominbyteArray = Files.readAllBytes(homin.toPath());
//
//        File hwang = new File("./src/test/resources/testImage/image_similarity_test/hwang.jpg");
//        byte[] hwangbyteArray = Files.readAllBytes(hwang.toPath());
//
//        File memeeman = new File("./src/test/resources/testImage/image_similarity_test/memeeman.jpg");
//        byte[] memeemanbyteArray = Files.readAllBytes(memeeman.toPath());
//
//        File pepe = new File("./src/test/resources/testImage/image_similarity_test/pepe.jpg");
//        byte[] pepebyteArray = Files.readAllBytes(pepe.toPath());
//
//        File rock = new File("./src/test/resources/testImage/image_similarity_test/rock.jpg");
//        byte[] rockbyteArray = Files.readAllBytes(rock.toPath());
//
//        File monday = new File("./src/test/resources/testImage/image_similarity_test/monday.jpg");
//        byte[] mondaybyteArray = Files.readAllBytes(monday.toPath());
//
////        System.out.println("---------------------만세------------------");
////        System.out.println(EnsembleModel.CalculateSimilarity(hurray1byteArray, hurray2byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(hurray1byteArray, hurray3byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(hurray2byteArray, hurray3byteArray));
////
////        System.out.println("----------------------안닮은거-----------------");
////        System.out.println(EnsembleModel.CalculateSimilarity(hurray1byteArray, grannybyteArray));
////
////        System.out.println("-----------------------팔짱----------------");
////        System.out.println(EnsembleModel.CalculateSimilarity(arms1byteArray, arms2byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(arms1byteArray, arms3byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(arms2byteArray, arms3byteArray));
////
////        System.out.println("---------------------얼굴------------------");
////        System.out.println(EnsembleModel.CalculateSimilarity(face1byteArray, face2byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(face1byteArray, face3byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(face1byteArray, face4byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(face2byteArray, face3byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(face2byteArray, face4byteArray));
////        System.out.println(EnsembleModel.CalculateSimilarity(face3byteArray, face4byteArray));
////
////        System.out.println("---------------------할머니총------------------");
////        System.out.println(EnsembleModel.CalculateSimilarity(gunnybyteArray, grannybyteArray));
////
////        System.out.println("---------------------할머니총 안닮은거------------------");
////        System.out.println(EnsembleModel.CalculateSimilarity(gunnybyteArray, rockbyteArray));
////
////        System.out.println("-------------------밈맨 락--------------------");
////        System.out.println(EnsembleModel.CalculateSimilarity(memeemanbyteArray, rockbyteArray));
////
////        System.out.println("---------------------밈맨 락 안닮은거------------------");
////        System.out.println(EnsembleModel.CalculateSimilarity(memeemanbyteArray, grannybyteArray));
//    }
//}