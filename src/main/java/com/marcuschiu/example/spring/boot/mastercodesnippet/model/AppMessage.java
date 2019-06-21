package com.marcuschiu.example.spring.boot.mastercodesnippet.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AppMessage {
    Integer snapshotPeriod;
    Integer fromNodeID;
    ArrayList<Integer> localState;
}
