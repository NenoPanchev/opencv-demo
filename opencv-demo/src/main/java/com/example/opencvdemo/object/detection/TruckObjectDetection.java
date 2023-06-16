package com.example.opencvdemo.object.detection;

import com.example.opencvdemo.config.AppYmlConfig;
import com.example.opencvdemo.constants.GlobalConstants;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.features2d.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
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

    public void showDifferenceBetweenTwoImages(String firstName, String secondName) {
        Mat img1 = Imgcodecs.imread(IMAGES_LOCATION + firstName);
        Mat img2 = Imgcodecs.imread(IMAGES_LOCATION + secondName);
        Mat edgedImage1 = getEdgedImage(img1);
        Mat edgedImage2 = getEdgedImage(img2);

        Mat diff = new Mat();
        Core.absdiff(edgedImage1, edgedImage2, diff);

        Mat mask = new Mat();
        Imgproc.cvtColor(diff, mask, Imgproc.COLOR_BGR2GRAY);

        double th = 1;
        Mat imask = new Mat();
        Core.compare(mask, new Scalar(th), imask, Core.CMP_GT);

        Mat canvas = new Mat(edgedImage2.size(), edgedImage2.type(), new Scalar(0, 0, 0));
        edgedImage2.copyTo(canvas, imask);
        viewImage(canvas);
    }

    public void compareImages(String firstName, String secondName) {
        Mat img1 = Imgcodecs.imread(IMAGES_LOCATION + firstName);
        Mat img2 = Imgcodecs.imread(IMAGES_LOCATION + secondName);

// Convert the images to grayscale
        Mat gray1 = new Mat();
        Mat gray2 = new Mat();
        Imgproc.cvtColor(img1, gray1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(img2, gray2, Imgproc.COLOR_BGR2GRAY);

        // Create ORB detector with 1000 keypoints and scaling pyramid factor of 1.2
        ORB detector = ORB.create(1000, 1.2f);
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();
        detector.detectAndCompute(gray1, new Mat(), keypoints1, descriptors1);
        detector.detectAndCompute(gray2, new Mat(), keypoints2, descriptors2);

        // Match the descriptors using BFMatcher
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors1, descriptors2, matches);
        List<DMatch> matchesList = matches.toList();


        // Filter the matches by distance
        double maxDist = 70;  // Adjust this threshold as needed
        List<DMatch> goodMatchesList = new ArrayList<>();
        for (DMatch match : matchesList) {
            if (match.distance < maxDist) {
                goodMatchesList.add(match);
            }
        }
        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(goodMatchesList);


        // Draw the matches
        Mat outputImage = new Mat();
        Features2d.drawMatches(
                img1, keypoints1, img2, keypoints2, goodMatches, outputImage,
                Scalar.all(-1), Scalar.all(-1), new MatOfByte(), Features2d.DrawMatchesFlags_DEFAULT
        );

        // Show the output image
        HighGui.imshow("Matches", outputImage);
        HighGui.waitKey();

        // Check if there are enough good matches
        if (goodMatches.toList().size() < 4) {
            System.out.println("Not enough good matches to calculate homography.");
            return;
        }
        // Estimate the transformation matrix
        MatOfPoint2f srcPoints = new MatOfPoint2f();
        MatOfPoint2f dstPoints = new MatOfPoint2f();
        KeyPoint[] keypoints1Array = keypoints1.toArray();
        KeyPoint[] keypoints2Array = keypoints2.toArray();
        for (DMatch match : goodMatches.toArray()) {
            Point pt1 = keypoints1Array[match.queryIdx].pt;
            Point pt2 = keypoints2Array[match.trainIdx].pt;
            srcPoints.push_back(new MatOfPoint2f(pt1));
            dstPoints.push_back(new MatOfPoint2f(pt2));
        }
        Mat transformationMatrix = Calib3d.findHomography(srcPoints, dstPoints);


        // Save the difference image
        viewImage(transformationMatrix);
    }
}
