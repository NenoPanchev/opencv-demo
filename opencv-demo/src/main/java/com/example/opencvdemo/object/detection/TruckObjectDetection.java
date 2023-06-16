package com.example.opencvdemo.object.detection;

import com.example.opencvdemo.config.AppYmlConfig;
import com.example.opencvdemo.constants.GlobalConstants;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.example.opencvdemo.utils.Utils.*;

@Component
public class TruckObjectDetection {
    private final AppYmlConfig appYmlConfig;
    private final String IMAGES_LOCATION = GlobalConstants.RESOURCES_PATH + "/truck/";
    private final String CREATED_IMAGES_LOCATION = IMAGES_LOCATION + "/reworked/";
    private final String REWORKED_POSTFIX = "reworked";

    public TruckObjectDetection(AppYmlConfig appYmlConfig) {
        this.appYmlConfig = appYmlConfig;
    }

    public void reworkAllImages() {
        List<String> imageNames = appYmlConfig.getTaskImageNames();

        for (String imageName : imageNames) {
            detectEdges(imageName);
        }
    }
    public void reworkImage(String imageName) {
        // Load the input image
        Mat image = createImageFromLocation(IMAGES_LOCATION, imageName);

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
        writeNewFile(image, CREATED_IMAGES_LOCATION, imageName, REWORKED_POSTFIX);
    }

    public void detectEdges(String imageName) {
        // Load the input image
        Mat image = createImageFromLocation(IMAGES_LOCATION, imageName);

        Mat gray = new Mat();
        Mat draw = new Mat();
        Mat wide = new Mat();

        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(gray, wide, 50, 150, 3, false);
        wide.convertTo(draw, CvType.CV_8U);

        viewImage(draw);
    }

    public void locatePapers(String imageName) {
        // Load the input image
        Mat image = createImageFromLocation(IMAGES_LOCATION, imageName);

        Mat gray = new Mat();
        Mat draw = new Mat();
        Mat wide = new Mat();

        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(gray, wide, 50, 150, 3, false);
        wide.convertTo(draw, CvType.CV_8U);


        // Convert the image to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Apply Canny edge detection to find edges
        Mat edges = new Mat();
        Imgproc.Canny(grayImage, edges, 50, 150);

        // Perform contour detection
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(wide, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Iterate over each contour
        for (MatOfPoint contour : contours) {
            // Approximate the contour to a polygon
            MatOfPoint2f approx = new MatOfPoint2f();
            double epsilon = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) * 0.02;
            Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), approx, epsilon, true);

            // If the polygon has 4 vertices, it is considered a square
            if (true) {
                Rect boundingRect = Imgproc.boundingRect(contour);

                // Draw a rectangle around the square
                Imgproc.rectangle(image, boundingRect.tl(), boundingRect.br(), new Scalar(0, 255, 0), 2);
            }
        }

        // Display the result
        viewImage(image);
    }

    public void detectFloor(String imageName) {
        String first = "first.jpg";
        String second = "second.jpg";
//
//        Mat image_a = Imgcodecs.imread(first);
//        Mat image_b = Imgcodecs.imread(second);
//
//        Mat gray_a = new Mat();
//        Mat gray_b = new Mat();
//        Imgproc.cvtColor(image_a, gray_a, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.cvtColor(image_b, gray_b, Imgproc.COLOR_BGR2GRAY);
//
//        Mat diff = new Mat();
//        Imgproc.compareSsim(gray_a, gray_b, diff, Imgproc.COMPARE_SSIM);
//
//        diff.convertTo(diff, CvType.CV_8U, 255.0);
//
//        Mat thresh = new Mat();
//        Imgproc.threshold(diff, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
//
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(thresh.clone(), contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        List<Pair<Double, MatOfPoint>> contour_sizes = new ArrayList<>();
//        for (MatOfPoint contour : contours) {
//            double area = Imgproc.contourArea(contour);
//            contour_sizes.add(new Pair<>(area, contour));
//        }
//
//        if (!contour_sizes.isEmpty()) {
//            Pair<Double, MatOfPoint> largest_contour = contour_sizes.stream()
//                    .max((a, b) -> Double.compare(a.first, b.first))
//                    .orElse(null);
//
//            if (largest_contour != null) {
//                Rect rect = Imgproc.boundingRect(largest_contour.second);
//                Imgproc.rectangle(image_a, rect.tl(), rect.br(), new Scalar(36, 255, 12), 2);
//                Imgproc.rectangle(image_b, rect.tl(), rect.br(), new Scalar(36, 255, 12), 2);
//            }
//        }
//
//        Imgcodecs.imwrite("image_a.jpg", image_a);
//        Imgcodecs.imwrite("image_b.jpg", image_b);
//        Imgcodecs.imwrite("thresh.jpg", thresh);
    }
}
