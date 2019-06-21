package com.marcuschiu.example.spring.boot.mastercodesnippet.configuration;

import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

@Data
public class Configuration {
    String fileName;

    Integer numNodes;
    Integer minPerActive;
    Integer maxPerActive;
    Integer minSendDelay;
    Integer snapshotDelay;
    Integer maxNumber;

    ArrayList<ConfigurationNodeInfo> configurationNodeInfos;

    public Configuration(File file) throws FileNotFoundException {
        fileName = file.getName().split("\\.")[0];

        ArrayList<String> lines = scrubConfigurationFile(file);

        String[] numbers = lines.get(0).split(" ");
        numNodes = Integer.parseInt(numbers[0]);
        minPerActive = Integer.parseInt(numbers[1]);
        maxPerActive = Integer.parseInt(numbers[2]);
        minSendDelay = Integer.parseInt(numbers[3]);
        snapshotDelay = Integer.parseInt(numbers[4]);
        maxNumber = Integer.parseInt(numbers[5]);

        configurationNodeInfos = new ArrayList<>();
        for(int i = 1; i <= numNodes; i++) {
            ConfigurationNodeInfo configurationNodeInfo = new ConfigurationNodeInfo();

            String[] nodeInfo = lines.get(i).split(" ");
            configurationNodeInfo.nodeID = Integer.parseInt(nodeInfo[0]);
            configurationNodeInfo.hostname = nodeInfo[1];
            configurationNodeInfo.port = nodeInfo[2];
            configurationNodeInfo.neighborNodeIDs = Arrays.stream(lines.get(i + numNodes).split(" "))
                                                    .map(Integer::parseInt)
                                                    .collect(Collectors.toCollection(ArrayList::new));

            configurationNodeInfo.nodeURL = "http://" + configurationNodeInfo.hostname + ":" + configurationNodeInfo.port;

            configurationNodeInfos.add(configurationNodeInfo);
        }
    }

    /**
     * remove empty lines and comments
     * @param file
     * @return ArrayList<String> list of valid lines
     * @throws FileNotFoundException
     */
    private ArrayList<String> scrubConfigurationFile(File file) throws FileNotFoundException {
        ArrayList<String> configurationLines = new ArrayList<>();

        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            // only lines starting with digit are valid
            if (line.length() > 0 && Character.isDigit(line.charAt(0))) {
                // remove comments from line (i.e. # followed by whatever
                int offset = line.indexOf("#");
                if (-1 != offset) {
                    line = line.substring(0, offset);
                }

                configurationLines.add(line);
            }
        }

        return configurationLines;
    }
}
