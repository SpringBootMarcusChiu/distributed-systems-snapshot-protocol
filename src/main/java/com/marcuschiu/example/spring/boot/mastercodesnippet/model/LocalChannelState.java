package com.marcuschiu.example.spring.boot.mastercodesnippet.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class LocalChannelState {
    Integer snapshotPeriod;
    Integer nodeID;
    ArrayList<AppMessage> channelState;
    ArrayList<Integer> localState;
    Integer numMarkerMessagesReceived;
    Boolean isActive;

    public Integer incAndGetNumMarkerMessagesReceived() {
        numMarkerMessagesReceived++;
        return numMarkerMessagesReceived;
    }
}
