package com.example.opencvdemo.utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageManipulator {
    private Mat image;

    public ImageManipulator(Mat image) {
        this.image = image;
    }

    public static ImageManipulator createImageFromLocation(String location, String fileName) {
        Mat image = Imgcodecs.imread(location + fileName);
        return new ImageManipulator(image);
    }

    public static ImageManipulator createImage(Mat image) {
        return new ImageManipulator(image);
    }

    public ImageManipulator applyGray() {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        return this;
    }

    public ImageManipulator applyCanny(int threshold1, int threshold2) {
        Imgproc.Canny(image, image, threshold1, threshold2);
        return this;
    }

    public ImageManipulator viewImage() {
        HighGui.imshow("Image view", image);
        HighGui.waitKey();
        HighGui.destroyAllWindows();
        return this;
    }

    public ImageManipulator applyThreshold(double thresh, double maxval, int type) {
        Imgproc.threshold(image, image, thresh, maxval, type);
        return this;
    }

    public ImageManipulator applyBitwiseNot() {
        Core.bitwise_not(image, image);
        return this;
    }

    public ImageManipulator applyMorphologicalOperationsToRemoveNoise(double width, double height) {
        // Apply morphological operations to remove noise
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(width, height));
        // Opening to remove small noise
        Mat opened = new Mat();
        Imgproc.morphologyEx(image, opened, Imgproc.MORPH_OPEN, kernel);
        // Closing to close small gaps
        Mat closed = new Mat();
        Imgproc.morphologyEx(opened, closed, Imgproc.MORPH_CLOSE, kernel);
        image = closed;
        return this;
    }

    public Mat build() {
        return image;
    }
}
