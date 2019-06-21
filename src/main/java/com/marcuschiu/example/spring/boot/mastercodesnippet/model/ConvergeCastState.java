package com.marcuschiu.example.spring.boot.mastercodesnippet.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ConvergeCastState {
    Integer numConvergeCastMessagesReceived;
    ArrayList<LocalChannelState> localChannelStates;

    public void incAndGetNumConvergeCastMessagesReceived() {
        numConvergeCastMessagesReceived++;
    }
}
