package com.example.opencvdemo.object.detection;

import com.example.opencvdemo.config.AppYmlConfig;
import com.example.opencvdemo.constants.GlobalConstants;
import com.example.opencvdemo.utils.ImageManipulator;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.features2d.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
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
            Mat image = createImageFromLocation(IMAGES_LOCATION, imageName);
            findShapesWithBGRemovalMarkerWay(image);
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
        detectEdges(image);
    }

    public void detectEdges(Mat image) {
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
//        Mat edgedImage1 = getEdgedImage(img1);
//        Mat edgedImage2 = getEdgedImage(img2);

        Mat diff = new Mat();
        Core.absdiff(img1, img2, diff);

        Mat mask = new Mat();
        Imgproc.cvtColor(diff, mask, Imgproc.COLOR_BGR2GRAY);

        double th = 15;
        Mat imask = new Mat();
        Core.compare(mask, new Scalar(th), imask, Core.CMP_GT);

        Mat canvas = new Mat(img2.size(), img2.type(), new Scalar(0, 0, 0));
        img2.copyTo(canvas, imask);
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


        writeNewFile(transformationMatrix, IMAGES_LOCATION, firstName, "merged");
        // Save the difference image
        viewImage(transformationMatrix);
    }

    public Mat drawContours(String imageName) {
        Mat image = createImageFromLocation(IMAGES_LOCATION, imageName);
        return drawContours(image);
    }

    public Mat drawContours(Mat image) {
        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Find Canny edges
        Mat edged = new Mat();
        Imgproc.Canny(image, edged, 1, 70);
//        Imgproc.adaptiveThreshold(gray, edged, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 181, -20);
        Imgproc.threshold(edged, edged, 1, 70, -1);
        // Finding contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        System.out.println("Number of Contours found = " + contours.size());
        List<MatOfPoint> bigContours = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            double area = Imgproc.contourArea(contour);
            System.out.println("Area: " + area);
//            if (area > 1100) {
                bigContours.add(contour);
//            }

            Point p = new Point((boundingRect.x + ((double) boundingRect.width / 2 - 30)), (boundingRect.y + (double) (boundingRect.height) / 2));
            Imgproc.putText(image, String.format("%.2f%%", area / 1000), p, Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0, 0, 200), 2);


        }
        // Draw all contours
//        Imgproc.drawContours(image, bigContours, -1, new Scalar(0, 255, 0), 3);

        return image;
    }

    public Mat applyBlur(Mat image, int blurIntensity) {

        Mat blurredImage = new Mat();
        Imgproc.blur(image, blurredImage, new Size(blurIntensity, blurIntensity));

        return blurredImage;
    }


    public Mat findShapesWithBGRemovalMarkerWay(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        // Apply blur
        Mat blurredImage = applyBlur(grayImage, 1);

        CLAHE clahe = Imgproc.createCLAHE(1.0, new Size(5, 5));
        Mat claheApplied = new Mat();
        clahe.apply(blurredImage, claheApplied);
        //Apply thresholding
        Mat thresholdImage = new Mat();
        // Create a structuring element for erosion
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat kernelD = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat eroded = new Mat();
        Imgproc.erode(claheApplied, eroded, kernel);
        Mat dilated = new Mat();
        Imgproc.dilate(eroded, dilated, kernelD);
//        Imgproc.Canny(dilated,thresholdImage,10,20);
        //normalize
//        if (image.channels() > 1) {
//        Imgproc.cvtColor(dilated, normalized, Imgproc.COLOR_BGR2GRAY);
//        } else {
//            normalized = dilated.clone(); // Use the existing grayscale image
//        }

//
        Imgproc.adaptiveThreshold(dilated, thresholdImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 181, -20);
//        Imgproc.threshold(dilated, thresholdImage, 200, 255, Imgproc.THRESH_OTSU);
//        Imgproc.threshold(dilated, thresholdImage, 10, 255, Imgproc.THRESH_BINARY);
        Mat equalized = new Mat();
        Imgproc.equalizeHist(thresholdImage, equalized);
//        Imgproc.threshold(dilated, thresholdImage, 110, 190, Imgproc.THRESH_BINARY);
//        Mat invertedImage = new Mat();
//        Core.bitwise_not(thresholdImage, invertedImage);

        //Find contours: Use the findContours()
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(thresholdImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        System.out.println("\ncontours");
        System.out.println(contours.size());

        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            int squareWidth = boundingRect.width;
            int squareHeight = boundingRect.height;

            // Calculate the size of the square
            int squareSize = Math.max(squareWidth, squareHeight);

            System.out.println("Square size: " + squareSize + " || ");
        }

        List<MatOfPoint> whiteSquareContours = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            double area = Imgproc.contourArea(contour);
            System.out.println("Square area: " + area + " || ");
            if (area > 100 && area < 500) {
                whiteSquareContours.add(contour);
            }


        }

        System.out.println("\nwhiteSquareContours");
        System.out.println(whiteSquareContours.size());


        List<Rect> rect = new ArrayList<>();
        int num = 1;
        for (MatOfPoint contour : whiteSquareContours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            rect.add(boundingRect);
            Point p = new Point((boundingRect.x + ((double) boundingRect.width / 2) - 10), (boundingRect.y + (double) (boundingRect.height) / 2) - 10);
            Imgproc.putText(image, String.valueOf(num), p, Imgproc.FONT_HERSHEY_COMPLEX, 0.5, new Scalar(0, 255, 255), 2);
            Imgproc.rectangle(image, boundingRect.tl(), boundingRect.br(), new Scalar(0, 255, 0), 2);

            num++;
        }
        Mat mask = Mat.zeros(thresholdImage.size(), thresholdImage.type());

        // Iterate over the contours and draw filled contours on the blank image

        Imgproc.drawContours(mask, whiteSquareContours, -1, new Scalar(255, 255, 255), Core.FILLED);

//        Imgproc.drawContours(image,whiteSquareContours,-1,new Scalar(255,255,255),1);
        calculateFullnessPercentage(image, rect);

        String filename = "/home/vladimir/Desktop/Detectionss.png";
        System.out.printf("Writing %s%n", filename);


        viewImage(claheApplied);

        viewImage(eroded);

        viewImage(dilated);

        viewImage(thresholdImage);

        viewImage(equalized);
        viewImage(mask);
        return mask;
    }

    public Mat absdiffBG(Mat image, Mat imageBG) {

        Mat foreGround = new Mat();
        Core.absdiff(image, imageBG, foreGround);
        viewImage(foreGround);
        return foreGround;

    }

    public Mat subtractBG(Mat image, Mat imageBG) {
        Mat subtract = new Mat();
        Core.subtract(image, imageBG, subtract);
        viewImage(subtract);
        return subtract;

    }

    public void max(Mat image) {
        Mat imageMax = new Mat();
        Core.max(image, new Scalar(10, 10, 10), imageMax);

        Mat foreground = imageMax.clone();

        Point seed = new Point(10, 10);  // Use the top left corner as a "background" seed color (assume pixel [10,10] is not in an object).

// Use floodFill for filling the background with black color
        Scalar newVal = new Scalar(7, 7, 7);
        Scalar loDiff = new Scalar(21, 21, 21, 21);
        Scalar upDiff = new Scalar(38, 38, 38, 38);
        Mat mask = new Mat();
        Rect rect = new Rect();
        try {
            Imgproc.floodFill(foreground, mask, seed, newVal, rect, loDiff, upDiff, Imgproc.FLOODFILL_FIXED_RANGE);
        } catch (CvException e) {
            e.printStackTrace();
        }

// Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(foreground, gray, Imgproc.COLOR_BGR2GRAY);

// Apply threshold
        Mat thresh = new Mat();
        Imgproc.threshold(gray, thresh, 30, 255, Imgproc.THRESH_BINARY);

// Use opening for removing small outliers
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new org.opencv.core.Size(5, 5));
        Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_OPEN, kernel);

// Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

// Draw contours
        Imgproc.drawContours(image, contours, -1, new Scalar(255, 0, 255), 3);

// Show images for testing


        viewImage(foreground);
        viewImage(gray);
        viewImage(thresh);
        viewImage(image);
    }

    public void calculateFullnessPercentage(Mat image, List<Rect> rect) {
        double areaOfContourRectangles = 0;
        for (Rect r :
                rect) {
            areaOfContourRectangles += r.height * r.width;
        }
        double percentageOfFullness = (areaOfContourRectangles / (image.height() * image.width())) * 100;
        System.out.printf("Percentage of fullness %,.2f " + "%" + "%n", percentageOfFullness);
    }

    public void calculateFullnessPercentageContour(Mat image, List<MatOfPoint> contour) {
        double areaOfContourRectangles = 0;
        for (MatOfPoint r :
                contour) {
            areaOfContourRectangles += Imgproc.contourArea(r);
        }
        double percentageOfFullness = (areaOfContourRectangles / (image.height() * image.width())) * 100;
        System.out.printf("Percentage of fullness %,.2f " + "%" + "%n", percentageOfFullness);
    }

    public Mat getFloorAndCalculateArea(Point topLeft, Point topRight, Point bottomLeft, Point bottomRight, Mat img) {
        int width = 352;
        int height = 288;
//        int height = 704;

        double leftSide = bottomLeft.y - topLeft.y;
        double rightSide = bottomRight.y - topRight.y;
        double topSide = topRight.x - topLeft.x;
        double bottomSide = bottomRight.x - bottomLeft.x;

        // Define the source and destination points
        MatOfPoint2f sourcePoints = new MatOfPoint2f(
                topLeft,
                topRight,
                bottomLeft,
                bottomRight
        );
        MatOfPoint2f destinationPoints = new MatOfPoint2f(
                new Point(0, 0),
                new Point(width, 0),
                new Point(0, height),
                new Point(width, height)
        );

        // Calculate the perspective transform matrix
        Mat matrix = Imgproc.getPerspectiveTransform(sourcePoints, destinationPoints);

        // Apply the perspective transform
        Mat imgOutput = new Mat();
        Imgproc.warpPerspective(img, imgOutput, matrix, new Size(width, height));


        viewImage(imgOutput);
        return imgOutput;
    }

    public Mat findShapesOfSecondImageMarkerWay(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        // Apply blur
        Mat blurredImage = applyBlur(grayImage, 1);

        CLAHE clahe = Imgproc.createCLAHE(1.0, new Size(5, 5));
        Mat claheApplied = new Mat();
        clahe.apply(blurredImage, claheApplied);
        //Apply thresholding
        Mat thresholdImage = new Mat();
        // Create a structuring element for erosion
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat kernelD = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat eroded = new Mat();
        Imgproc.erode(claheApplied, eroded, kernel);
        Mat dilated = new Mat();
        Imgproc.dilate(eroded, dilated, kernelD);
        Imgproc.adaptiveThreshold(dilated, thresholdImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 181, -20);
        Mat equalized = new Mat();
        Imgproc.equalizeHist(thresholdImage, equalized);

        //Find contours: Use the findContours()
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(thresholdImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        System.out.println("\ncontours");
        System.out.println(contours.size());

        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            int squareWidth = boundingRect.width;
            int squareHeight = boundingRect.height;

            // Calculate the size of the square
            int squareSize = Math.max(squareWidth, squareHeight);

            System.out.println("Square size: " + squareSize + " || ");
        }

        List<MatOfPoint> whiteSquareContours = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            double area = Imgproc.contourArea(contour);
            System.out.println("Square area: " + area + " || ");
            if (area > 100) {
                whiteSquareContours.add(contour);
            }


        }

        System.out.println("\nwhiteSquareContours");
        System.out.println(whiteSquareContours.size());


        List<Rect> rect = new ArrayList<>();
        int num = 1;
        for (MatOfPoint contour : whiteSquareContours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            rect.add(boundingRect);
            Point p = new Point((boundingRect.x + ((double) boundingRect.width / 2) - 10), (boundingRect.y + (double) (boundingRect.height) / 2) - 10);
            Imgproc.putText(image, String.valueOf(num), p, Imgproc.FONT_HERSHEY_COMPLEX, 0.5, new Scalar(0, 255, 255), 2);
            Imgproc.rectangle(image, boundingRect.tl(), boundingRect.br(), new Scalar(255, 255, 255), 2);

            num++;
        }
        Imgproc.drawContours(image, contours, -1, new Scalar(255, 0, 255), 3, Core.FILLED);
        calculateFullnessPercentageContour(image, contours);
        viewImage(claheApplied);
        viewImage(eroded);
        viewImage(dilated);
        viewImage(thresholdImage);

        return thresholdImage;
    }

    public void transformAndCompareImages(String firstName, String secondName) {
        Mat image1 = createImageFromLocation(IMAGES_LOCATION, firstName);
        Mat image2 = createImageFromLocation(IMAGES_LOCATION, secondName);

        Mat transformed1 = getFloorAndCalculateArea(new Point(152, 121),
                new Point(240, 118),
                new Point(63, 287),
                new Point(341, 287),
                image1);
        Mat transformed2 = getFloorAndCalculateArea(new Point(152, 121),
                new Point(240, 118),
                new Point(63, 287),
                new Point(341, 287),
                image2);

        Mat shapesWithBGRemoval1 = findShapesWithBGRemovalMarkerWay(transformed1);
        Mat shapesWithBGRemoval2 = findShapesOfSecondImageMarkerWay(transformed2);
        Mat result = new Mat();
        Core.bitwise_and(shapesWithBGRemoval1, shapesWithBGRemoval2, result);
        viewImage(result);
        System.out.println();
    }

    public void transformContourAndCompareImages(String firstName, String secondName) {
        Mat image1 = createImageFromLocation(IMAGES_LOCATION, firstName);
        Mat image2 = createImageFromLocation(IMAGES_LOCATION, secondName);
        viewImage(image1);
        viewImage(image2);

        Mat transformed1 = getFloorAndCalculateArea(new Point(152, 121),
                new Point(240, 118),
                new Point(63, 287),
                new Point(341, 287),
                image1);
        Mat transformed2 = getFloorAndCalculateArea(new Point(152, 121),
                new Point(240, 118),
                new Point(63, 287),
                new Point(341, 287),
                image2);
        writeNewFile(transformed2, CREATED_IMAGES_LOCATION, firstName, "transformed");

// Compute absolute difference
        Mat diff = new Mat();
        Core.absdiff(transformed1, transformed2, diff);

        Mat mask = ImageManipulator.createImage(diff)
                .viewImage()
                .applyThreshold(1, 255, Imgproc.THRESH_BINARY)
                .viewImage()
                .applyBitwiseNot()
                .viewImage()
                .applyMorphologicalOperationsToRemoveNoise(10, 10)
                .viewImage()
                .applyBitwiseNot()
                .build();

        calculateAreas(mask);
        Mat contours = drawContours(mask);
        viewImage(contours);
        writeNewFile(mask, CREATED_IMAGES_LOCATION, firstName, "loaded");
        System.out.println();
    }

    public void calculateAreas(Mat mask) {
        // Convert the mask to grayscale
        Mat grayscaleMask = new Mat();
        Imgproc.cvtColor(mask, grayscaleMask, Imgproc.COLOR_BGR2GRAY);

// Get the total number of pixels in the mask
        int totalPixels = grayscaleMask.rows() * grayscaleMask.cols();

// Calculate the number of white pixels using countNonZero
        int whitePixels = Core.countNonZero(grayscaleMask);

// Calculate the number of black pixels
        int blackPixels = totalPixels - whitePixels;

// Calculate the area percentages
        double whiteAreaPercentage = (double) whitePixels / totalPixels * 100.0;
        double blackAreaPercentage = (double) blackPixels / totalPixels * 100.0;

// Print the results
        System.out.printf("Loaded Area Percentage: %.2f%%%n", whiteAreaPercentage);
        System.out.printf("Free Area Percentage: %.2f%%%n", blackAreaPercentage);
    }

    public Mat findShapesWithoutMarker(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        // Apply blur
        Mat blurredImage = applyBlur(grayImage, 10);

        CLAHE clahe = Imgproc.createCLAHE(1.0, new Size(5, 5));
        Mat claheApplied = new Mat();
        clahe.apply(blurredImage, claheApplied);
        //Apply thresholding
        Mat thresholdImage = new Mat();
        // Create a structuring element for erosion
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat kernelD = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat eroded = new Mat();
        Imgproc.erode(claheApplied, eroded, kernel);
        Mat dilated = new Mat();
        Imgproc.dilate(eroded, dilated, kernelD);

        Imgproc.adaptiveThreshold(dilated, thresholdImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 181, -20);

//        Imgproc.Canny(thresholdImage, thresholdImage, 10, 250);
        Mat equalized = new Mat();
        Imgproc.equalizeHist(thresholdImage, equalized);

        //Find contours: Use the findContours()
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(thresholdImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        System.out.println("\ncontours");
        System.out.println(contours.size());

        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            int squareWidth = boundingRect.width;
            int squareHeight = boundingRect.height;

            // Calculate the size of the square
            int squareSize = Math.max(squareWidth, squareHeight);

            System.out.println("Square size: " + squareSize + " || ");
        }

        List<MatOfPoint> whiteSquareContours = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            double area = Imgproc.contourArea(contour);
            System.out.println("Square area: " + area + " || ");
            if (area > 100) {
                whiteSquareContours.add(contour);
            }
//            whiteSquareContours.add(contour);


        }

        System.out.println("\nwhiteSquareContours");
        System.out.println(whiteSquareContours.size());


        List<Rect> rect = new ArrayList<>();
        int num = 1;
        for (MatOfPoint contour : whiteSquareContours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            rect.add(boundingRect);
            Point p = new Point((boundingRect.x + ((double) boundingRect.width / 2) - 10), (boundingRect.y + (double) (boundingRect.height) / 2) - 10);
            Imgproc.putText(image, String.valueOf(num), p, Imgproc.FONT_HERSHEY_COMPLEX, 0.5, new Scalar(0, 255, 255), 2);
            Imgproc.rectangle(image, boundingRect.tl(), boundingRect.br(), new Scalar(0, 255, 0), 2);

            num++;
        }
        Mat mask = Mat.zeros(thresholdImage.size(), thresholdImage.type());

        // Iterate over the contours and draw filled contours on the blank image

        Imgproc.drawContours(mask, whiteSquareContours, -1, new Scalar(255, 255, 255), Core.FILLED);

//        Imgproc.drawContours(image,whiteSquareContours,-1,new Scalar(255,255,255),1);
        calculateFullnessPercentage(image, rect);
        viewImage(claheApplied);
        viewImage(eroded);
        viewImage(dilated);
        viewImage(thresholdImage);
        viewImage(equalized);
        viewImage(mask);
        return mask;
    }
}
