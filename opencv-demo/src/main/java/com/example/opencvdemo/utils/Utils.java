package com.example.opencvdemo.utils;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Utils {

    public static void writeNewFile(Mat image, String location, String fileName, String postfix) {
        Imgcodecs.imwrite(location + generateNewFileName(fileName, postfix), image);
    }
    public static Mat createImageFromLocation(String fileLocation, String fileName) {
        String location = fileLocation + fileName;
        return Imgcodecs.imread(location);
    }

    public static String generateNewFileName(String fileName, String postfix) {
        String oldName = fileName.substring(0, fileName.indexOf("."));
        String extension = fileName.substring(fileName.indexOf("."));
        return oldName + "-" + postfix + extension;
    }

    public static void viewImage(Mat image) {
        // Display the output image
        HighGui.imshow("Image view", image);
        HighGui.waitKey();
        HighGui.destroyAllWindows();
    }

    public static void printASCII(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            int asciiValue = (int) ch;
            System.out.println("Character: " + ch + ", ASCII value: " + asciiValue);
        }
    }

    public static Mat getGrayImage(Mat image) {
        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }

    public static Mat getEdgedImage(Mat image) {
        Mat gray = new Mat();
        Mat draw = new Mat();
        Mat wide = new Mat();

        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(gray, wide, 50, 150, 3, false);
        wide.convertTo(draw, CvType.CV_8U);
        return wide;
    }
}
