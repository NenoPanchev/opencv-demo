package com.example.opencvdemo.video.shapedetection;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Component;

import javax.swing.*;
@Component
public class ShapeDetection {

    public void startShapeDetection() {
        // Create panels
        final JPanel cameraFeed = new JPanel();
        final JPanel processedFeed = new JPanel();
        ShapeDetectionUtil.createJFrame(cameraFeed, processedFeed);

        // Create video capture object (index 0 is default camera)
        final VideoCapture camera = new VideoCapture(0);

        // Start shape detection
        start(cameraFeed, processedFeed, camera).run();
    }

    private Runnable start(final JPanel cameraFeed,
                                                final JPanel processedFeed,
                                                final VideoCapture camera) {
        return () -> {
            final Mat frame = new Mat();

            while (true) {
                // Read frame from camera
                camera.read(frame);

                // Process frame
                final Mat processed = ShapeDetectionUtil.processImage(frame);

                // Mark outer contour
                ShapeDetectionUtil.markOuterContour(processed, frame);

                // Draw current frame
                ShapeDetectionUtil.drawImage(frame, cameraFeed);

                // Draw current processed image (for debugging)
                ShapeDetectionUtil.drawImage(processed, processedFeed);
            }
        };
    }
}
