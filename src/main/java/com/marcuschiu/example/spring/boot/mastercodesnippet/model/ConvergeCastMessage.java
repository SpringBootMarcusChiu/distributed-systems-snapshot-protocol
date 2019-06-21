package com.marcuschiu.example.spring.boot.mastercodesnippet.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ConvergeCastMessage {
    Integer snapshotPeriod;
    ArrayList<LocalChannelState> localChannelStates;
}
