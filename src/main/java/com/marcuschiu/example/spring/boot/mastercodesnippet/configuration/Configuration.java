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
    Integer numNodes;
    Integer minPerActive;
    Integer maxPerActive;
    Integer minSendDelay;
    Integer snapshotDelay;
    Integer maxNumber;

    ArrayList<ConfigurationNodeInfo> configurationNodeInfos;

    public Configuration(File file) throws FileNotFoundException {
        ArrayList<String> lines = scrubConfigurationFile(file);

        String[] numbers = lines.get(0).split(" ");
        this.numNodes = Integer.parseInt(numbers[0]);
        this.minPerActive = Integer.parseInt(numbers[1]);
        this.maxPerActive = Integer.parseInt(numbers[2]);
        this.minSendDelay = Integer.parseInt(numbers[3]);
        this.snapshotDelay = Integer.parseInt(numbers[4]);
        this.maxNumber = Integer.parseInt(numbers[5]);

        this.configurationNodeInfos = new ArrayList<>();
        for(int i = 1; i <= this.numNodes; i++) {
            ConfigurationNodeInfo configurationNodeInfo = new ConfigurationNodeInfo();

            String[] nodeInfo = lines.get(i).split(" ");
            configurationNodeInfo.nodeID = nodeInfo[0];
            configurationNodeInfo.hostname = nodeInfo[1];
            configurationNodeInfo.port = nodeInfo[2];
            configurationNodeInfo.neighbors = Arrays.stream(lines.get(i + this.numNodes).split(" "))
                                                    .map(Integer::parseInt)
                                                    .collect(Collectors.toCollection(ArrayList::new));

            configurationNodeInfo.nodeURL = "http://" + configurationNodeInfo.hostname + ":" + configurationNodeInfo.port;
            configurationNodeInfo.messageURL = configurationNodeInfo.nodeURL + "/api/application/message";

            this.configurationNodeInfos.add(configurationNodeInfo);
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
