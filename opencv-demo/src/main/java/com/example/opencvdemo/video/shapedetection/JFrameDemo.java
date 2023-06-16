package com.example.opencvdemo.video.shapedetection;

import javax.swing.*;
import java.awt.*;

public class JFrameDemo {
    private JFrame frame;

    public JFrameDemo() {
        initialize();
    }

    private void initialize() {
        frame= new JFrame();
        frame.setLayout(new BorderLayout(10, 5));
        frame.setTitle("Java Swing JPanel Demo");
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        frame.add(panel, BorderLayout.CENTER);
        
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new JFrameDemo();
    }
}
