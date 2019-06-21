package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.LocalChannelState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class FileService {

    private final String filePrefix;
    private final String outputDirectoryPath = "output/";

    @Autowired
    public FileService(Configuration configuration) {
        filePrefix = configuration.getFileName();
        cleanOutputDirectory(outputDirectoryPath);
    }

    private void cleanOutputDirectory(String outputDirectoryPath) {
        File directory = new File(outputDirectoryPath);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (!file.getName().equals(".gitkeep")) {
                file.delete();
            }
        }
    }

    public void writeSnapshot(ArrayList<LocalChannelState> localChannelStates) {

    }

    public void whenWriteStringUsingBufferedWritter_thenCorrect() throws IOException {
        String str = "Hello";
        BufferedWriter writer = new BufferedWriter(new FileWriter("output/"+ filePrefix +".txt", true));
        writer.write(str);
        writer.write(str);
        writer.write(str);
        writer.close();
    }
}
