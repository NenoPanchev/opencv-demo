package com.example.opencvdemo.manipulation;

import com.example.opencvdemo.init.AppInit;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.example.opencvdemo.init.AppInit.FILE_NAME;
import static com.example.opencvdemo.init.AppInit.LOCATION;
import static com.example.opencvdemo.utils.Utils.createImageFromLocation;
import static com.example.opencvdemo.utils.Utils.writeNewFile;

@Component
public class ImageManipulation {

    public void drawContours() {
        Mat image = createImageFromLocation(LOCATION, AppInit.FILE_NAME);

        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Find Canny edges
        Mat edged = new Mat();
        Imgproc.Canny(gray, edged, 30, 200);

        // Finding contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edged, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        System.out.println("Number of Contours found = " + contours.size());

        // Draw all contours
        Imgproc.drawContours(image, contours, -1, new Scalar(0, 255, 0), 3);

        // Save the image with contours to a new file
        writeNewFile(image, LOCATION, FILE_NAME, "contours");

    }

    public void makeGray() {
        Mat image = createImageFromLocation(LOCATION, FILE_NAME);
        // prepare to convert a RGB image in gray scale
        System.out.println("Convert the image in gray scale... ");
        // get the jpeg image from the internal resource folder
        // convert the image in gray scale
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);

        // write the new image on disk
        writeNewFile(image, LOCATION, FILE_NAME, "gray");
    }

    public void sketch() {
        Mat image = createImageFromLocation(LOCATION, FILE_NAME);
        // Convert the image to grayscale
        Mat imgGray = new Mat();
        Imgproc.cvtColor(image, imgGray, Imgproc.COLOR_BGR2GRAY);

        // Apply Gaussian blur
        Mat imgGrayBlur = new Mat();
        Imgproc.GaussianBlur(imgGray, imgGrayBlur, new Size(5, 5), 0);

        // Detect edges using Canny
        Mat cannyEdges = new Mat();
        Imgproc.Canny(imgGrayBlur, cannyEdges, 10, 70);

        // Create a binary inverted mask
        Mat mask = new Mat();
        Imgproc.threshold(cannyEdges, mask, 70, 255, Imgproc.THRESH_BINARY_INV);

        // Display or save the resulting mask image
        writeNewFile(mask, LOCATION, FILE_NAME, "mask");
    }
}
