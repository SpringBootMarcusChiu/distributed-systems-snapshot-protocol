package com.marcuschiu.example.spring.boot.mastercodesnippet.configuration;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ConfigurationNodeInfo {

    String nodeID;
    String hostname;
    String port;

    String nodeURL;

    ArrayList<Integer> neighbors;
}
