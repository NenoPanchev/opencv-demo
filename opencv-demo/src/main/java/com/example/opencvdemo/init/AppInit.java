package com.example.opencvdemo.init;


import com.example.opencvdemo.object.detection.TruckObjectDetection;
import com.example.opencvdemo.video.shapedetection.ShapeDetection;
import com.example.opencvdemo.manipulation.ImageManipulation;
import com.example.opencvdemo.object.detection.ObjectDetection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppInit implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppInit.class);
    private final ImageManipulation imageManipulation;
    private final ObjectDetection objectDetection;
    private final TruckObjectDetection truckObjectDetection;
    private final ShapeDetection shapeDetection;
    public static final String LOCATION = "/home/neno/IdeaProjects/OpenCV Demo/demo-2/src/main/resources/";
    public static final String FILE_NAME = "coffee.jpg";

    public AppInit(ImageManipulation imageManipulation, ObjectDetection objectDetection, TruckObjectDetection truckObjectDetection, ShapeDetection shapeDetection) {
        this.imageManipulation = imageManipulation;
        this.objectDetection = objectDetection;
        this.truckObjectDetection = truckObjectDetection;
        this.shapeDetection = shapeDetection;
    }

    @Override
    public void run(String... args) throws Exception {
        nu.pattern.OpenCV.loadLocally();
        System.setProperty("java.awt.headless", "false");
//        OpenCV.loadShared();
        LOGGER.info("info");
        LOGGER.warn("warn");
        LOGGER.error("error");
//        imageManipulation.makeGray();
//        imageManipulation.sketch();
//        imageManipulation.drawContours();
//        objectDetection.findByTemplate();
//        objectDetection.findFace();
//        objectDetection.findFaceAndEyes();
//        truckObjectDetection.reworkAllImages();
//        shapeDetection.startShapeDetection();
//        truckObjectDetection.showDifferenceBetweenTwoImages("ниска осветеност (1).png", "ниска осветеност (2).png");
//        truckObjectDetection.showDifferenceBetweenTwoImages("осветно_(1).png", "осветно_(2).png");
//        truckObjectDetection.compareImages("ниска осветеност (1).png", "ниска осветеност (2).png");
//        truckObjectDetection.compareImages("осветно_(1)-reworked.png", "осветно_(2)-reworked.png");
            truckObjectDetection.transformContourAndCompareImages("ниска осветеност (2).png", "ниска осветеност (3).png");
//            truckObjectDetection.transformAndCompareImages("ниска осветеност (1).png", "ниска осветеност (1)-loaded.png");
            truckObjectDetection.transformContourAndCompareImages("ниска осветеност (1).png", "ниска осветеност (1)-loaded.png");
            truckObjectDetection.transformContourAndCompareImages("ниска осветеност (1).png", "ниска осветеност (1)-few.png");
            truckObjectDetection.transformContourAndCompareImages("ниска осветеност (1).png", "ниска осветеност (1)-lots.png");
//        String first = "генериране";
//        String second = "генериране";
//        printASCII(first);
//        printASCII(second);
    }
}
