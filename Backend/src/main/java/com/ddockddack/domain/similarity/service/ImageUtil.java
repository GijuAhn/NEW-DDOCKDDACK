package com.ddockddack.domain.similarity.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import static org.opencv.imgcodecs.Imgcodecs.imdecode;
import org.opencv.imgcodecs.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.opencv.core.CvType.CV_8UC;

// ByteArray ↔ ByteArrayInputStream ↔ InputStream ↔ ImageIO.read(InputStream) ↔ BufferedImage ↔ MAT(※opencv matrix object) ↔ Graphics2D
public class ImageUtil {
//    image format must be a jpg

    //    BufferedImage to Mat
    public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
    }

    //    Mat to BufferedImage
    public static BufferedImage Mat2BufferedImage(Mat matrix) throws IOException {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
    }


    //  converting byte[] image data (from frontend) to InputStream, and vice versa.
    //  byte array to inputStream
    public static InputStream ByteArray2InputStream(byte[] byteArray){
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        return inputStream;
    }

    //    inputStream to byte array
    public static byte[] InputStream2ByteArray(InputStream inputStream) {
        final byte[] byteArray;
        try (inputStream) {
            byteArray = inputStream.readAllBytes();
            return byteArray;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


//    BufferedImage Resizer
    public enum Resizer {
        NEAREST_NEIGHBOR {
            @Override
            public BufferedImage resize(BufferedImage source,
                                        int width, int height) {
                return commonResize(source, width, height,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            }
        },
        BILINEAR {
            @Override
            public BufferedImage resize(BufferedImage source,
                                        int width, int height) {
                return commonResize(source, width, height,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
        },
        BICUBIC {
            @Override
            public BufferedImage resize(BufferedImage source,
                                        int width, int height) {
                return commonResize(source, width, height,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            }
        },
        PROGRESSIVE_BILINEAR {
            @Override
            public BufferedImage resize(BufferedImage source,
                                        int width, int height) {
                return progressiveResize(source, width, height,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
        },
        PROGRESSIVE_BICUBIC {
            @Override
            public BufferedImage resize(BufferedImage source,
                                        int width, int height) {
                return progressiveResize(source, width, height,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            }
        },
        AVERAGE {
            @Override
            public BufferedImage resize(BufferedImage source,
                                        int width, int height) {
                Image img2 = source.getScaledInstance(width, height,
                        Image.SCALE_AREA_AVERAGING);
                BufferedImage img = new BufferedImage(width, height,
                        source.getType());
                Graphics2D g = img.createGraphics();
                try {
                    g.drawImage(img2, 0, 0, width, height, null);
                } finally {
                    g.dispose();
                }
                return img;
            }
        };

        public abstract BufferedImage resize(BufferedImage source,
                                             int width, int height);

        private static BufferedImage progressiveResize(BufferedImage source,
                                                       int width, int height, Object hint) {
            int w = Math.max(source.getWidth()/2, width);
            int h = Math.max(source.getHeight()/2, height);
            BufferedImage img = commonResize(source, w, h, hint);
            while (w != width || h != height) {
                BufferedImage prev = img;
                w = Math.max(w/2, width);
                h = Math.max(h/2, height);
                img = commonResize(prev, w, h, hint);
                prev.flush();
            }
            return img;
        }

        private static BufferedImage commonResize(BufferedImage source,
                                                  int width, int height, Object hint) {
            BufferedImage img = new BufferedImage(width, height,
                    source.getType());
            Graphics2D g = img.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
                g.drawImage(source, 0, 0, width, height, null);
            } finally {
                g.dispose();
            }
            return img;
        }
    };
}