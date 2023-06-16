package com.example.opencvdemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("app")
public class AppYmlConfig {
    private List<String> taskImageNames;

    public AppYmlConfig() {
    }

    public List<String> getTaskImageNames() {
        return taskImageNames;
    }

    public void setTaskImageNames(List<String> taskImagesNames) {
        this.taskImageNames = taskImagesNames;
    }
}
