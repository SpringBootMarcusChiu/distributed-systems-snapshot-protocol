package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.LocalChannelState;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@Service
public class PerpetualSnapshotTakingService {

    @Autowired
    Configuration configuration;

    @Autowired
    FileService fileService;

    @Autowired
    EventService eventService;

    @Autowired
    StateService stateService;

    private volatile AtomicBoolean allNodesFinishedMapProtocol = new AtomicBoolean(false);

    public void runPerpetualSnapshotTaking() throws InterruptedException {
        System.out.println("PERPETUAL SNAPSHOT CAPTURE - THREAD START");
        Integer delay = configuration.getSnapshotDelay();
        while (true) {
            Thread.sleep(delay);
            if (!allNodesFinishedMapProtocol.get()) {
                eventService.selfInitiateSnapshot();
            } else {
                break;
            }
        }
        System.out.println("PERPETUAL SNAPSHOT CAPTURE - THREAD END");
    }

    public void receivedAllLocalChannelStates(Integer snapshotPeriod, ArrayList<LocalChannelState> localChannelStates) {
        System.out.println("SNAPSHOT PERIOD: " + snapshotPeriod + " - COMPLETED");
        if (!allNodesFinishedMapProtocol.get()) {
            if (isMapProtocolFinishedInAllNodes(localChannelStates)) {
                allNodesFinishedMapProtocol.set(true);

                // send shutdown message to all via marker messages
                stateService.selfInitiateShutdown();
            }
            fileService.writeSnapshot(localChannelStates);
        } else {
            System.out.println("EXTRA - WILL NOT SAVE TO FILE");
        }
    }

    private Boolean isMapProtocolFinishedInAllNodes(ArrayList<LocalChannelState> localChannelStates) {
        for (LocalChannelState localChannelState : localChannelStates) {
            if (localChannelState.getIsActive() || localChannelState.getChannelState().size() > 0) {
                return false;
            }
        }
        return true;
    }
}
