package com.marcuschiu.example.spring.boot.mastercodesnippet.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class MarkerMessageResponse {
    Boolean isDuplicate;
    Integer snapshotPeriod;
    Integer fromNodeID;
    ArrayList<MarkerMessageResponseLocal> markerMessageResponseLocals;
}
