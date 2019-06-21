package com.marcuschiu.example.spring.boot.mastercodesnippet.configuration;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ConfigurationNodeInfo {

    Integer nodeID;
    String hostname;
    String port;

    String nodeURL;

    ArrayList<Integer> neighborNodeIDs;
}
