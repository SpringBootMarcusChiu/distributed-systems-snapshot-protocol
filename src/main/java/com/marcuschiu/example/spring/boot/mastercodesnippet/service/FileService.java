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
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class FileService {

    // better than synchronized (intrinsic lock)
    // checks the queued threads and gives priority access to the longest waiting one
    private final ReentrantLock reLock = new ReentrantLock(true);

    private final String filePrefix;
    private final String outputDirectoryPath = "output/";
    private HashMap<Integer, String> outputFileNames;

    @Autowired
    public FileService(Configuration configuration) {
        filePrefix = configuration.getFileName();
        outputFileNames = new HashMap<>();
        for (int i = 0; i < configuration.getNumNodes(); i++) {
            String f = "output/"+ filePrefix + "-" + i + ".txt";
            outputFileNames.put(i,f);
        }

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
        reLock.lock();
        try {
            synchronizedWriteSnapshot(localChannelStates);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reLock.unlock();
        }
    }

    private void synchronizedWriteSnapshot(ArrayList<LocalChannelState> localChannelStates) throws IOException {
        Integer nodeID;

        for (LocalChannelState localChannelState : localChannelStates) {
            nodeID = localChannelState.getNodeID();
            StringBuilder line = new StringBuilder();
            for (Integer i : localChannelState.getLocalState()) {
                line.append(i.toString()).append(" ");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileNames.get(nodeID), true))) {
                writer.write(line.append("\n").toString());
            }
        }
    }
}
