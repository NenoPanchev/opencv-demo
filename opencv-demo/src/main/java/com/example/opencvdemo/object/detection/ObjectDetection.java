package com.example.opencvdemo.object.detection;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Component;

@Component
public class ObjectDetection {
    public static final String FULL_IMAGE = "/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/waldo/WaldoBeach.jpg";
    public static final String TEMPLATE = "/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/waldo/waldo.jpg";

    public void findByTemplate() {
        // Load input image and convert to grayscale
        Mat image = Imgcodecs.imread(FULL_IMAGE);
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Load template image
        Mat template = Imgcodecs.imread(TEMPLATE, Imgcodecs.IMREAD_GRAYSCALE);

        // Perform template matching
        Mat result = new Mat();
        Imgproc.matchTemplate(gray, template, result, Imgproc.TM_CCOEFF);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // Find best match location
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point maxLoc = mmr.maxLoc;

        // Create bounding box
        Rect boundingBox = new Rect((int) maxLoc.x, (int) maxLoc.y, template.cols(), template.rows());

        // Draw bounding box on the image
        Imgproc.rectangle(image, boundingBox.tl(), boundingBox.br(), new Scalar(0, 0, 255), 5);

        // Show the image with bounding box
        Imgcodecs.imwrite("/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/waldo/WaldoBeach-found.jpg", image);
    }

    public void findFace() {
        // Load the face classifier
        CascadeClassifier faceClassifier = new CascadeClassifier("/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/haarcascades/haarcascade_frontalface_default.xml");

        // Load the input image
        Mat image = Imgcodecs.imread("/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/faces/Hillary.jpg");

        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Perform face detection
        MatOfRect faces = new MatOfRect();
        faceClassifier.detectMultiScale(gray, faces, 1.3, 5);

        // When no faces detected, faces.toArray() returns an empty array
        if (faces.toArray().length == 0) {
            System.out.println("No faces found");
        }

        // Draw rectangles around the detected faces
        for (Rect rect : faces.toArray()) {
            Imgproc.rectangle(image, rect.tl(), rect.br(), new Scalar(127, 0, 255), 2);
        }

        // Display the image with face detection
        Imgcodecs.imwrite("/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/faces/Hillary-face-detected.jpg", image);
    }

    public void findFaceAndEyes() {
        // Load the face and eye classifiers
        CascadeClassifier faceClassifier = new CascadeClassifier("/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/haarcascades/haarcascade_frontalface_default.xml");
        CascadeClassifier eyeClassifier = new CascadeClassifier("/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/haarcascades/haarcascade_eye.xml");

        // Load the input image
        Mat image = Imgcodecs.imread("/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/faces/Trump.jpg");

        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Perform face detection
        MatOfRect faces = new MatOfRect();
        faceClassifier.detectMultiScale(gray, faces, 1.3, 5);

        // When no faces detected, faces.toArray() returns an empty array
        if (faces.toArray().length == 0) {
            System.out.println("No faces found");
        }

        // Iterate over the detected faces
        for (Rect faceRect : faces.toArray()) {
            // Draw rectangle around the face
            Imgproc.rectangle(image, faceRect.tl(), faceRect.br(), new Scalar(127, 0, 255), 2);

            // Extract the region of interest (face) from the grayscale image
            Mat faceROI = gray.submat(faceRect);

            // Perform eye detection on the face region
            MatOfRect eyes = new MatOfRect();
            eyeClassifier.detectMultiScale(faceROI, eyes, 1.2, 5);

            // Iterate over the detected eyes within the face region
            for (Rect eyeRect : eyes.toArray()) {
                // Draw rectangle around the eye (relative to the face region)
                Point eyeRectTopLeft = new Point(faceRect.x + eyeRect.x, faceRect.y + eyeRect.y);
                Point eyeRectBottomRight = new Point(faceRect.x + eyeRect.x + eyeRect.width, faceRect.y + eyeRect.y + eyeRect.height);
                Imgproc.rectangle(image, eyeRectTopLeft, eyeRectBottomRight, new Scalar(255, 255, 0), 2);
            }
        }

        // Display the image with face and eye detection
        Imgcodecs.imwrite("/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/faces/Trump-face-eyes.jpg", image);
    }

    private void detectPaperOnFloor() {

    }
}
