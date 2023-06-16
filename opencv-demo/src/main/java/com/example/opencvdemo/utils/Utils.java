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
    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame
     *            the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    public static Image mat2Image(Mat frame)
    {
        try
        {
            return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
        }
        catch (Exception e)
        {
            System.err.println("Cannot convert the Mat obejct: " + e);
            return null;
        }
    }

    /**
     * Generic method for putting element running on a non-JavaFX thread on the
     * JavaFX thread, to properly update the UI
     *
     * @param property
     *            a {@link ObjectProperty}
     * @param value
     *            the value to set for the given {@link ObjectProperty}
     */
    public static <T> void onFXThread(final ObjectProperty<T> property, final T value)
    {
        Platform.runLater(() -> {
            property.set(value);
        });
    }

    /**
     * Support for the {@link mat2image()} method
     *
     * @param original
     *            the {@link Mat} object in BGR or grayscale
     * @return the corresponding {@link BufferedImage}
     */
    private static BufferedImage matToBufferedImage(Mat original)
    {
        // init
        BufferedImage image = null;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1)
        {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        }
        else
        {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

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
