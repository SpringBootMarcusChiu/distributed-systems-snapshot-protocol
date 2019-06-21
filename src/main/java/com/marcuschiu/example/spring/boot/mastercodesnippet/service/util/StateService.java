package com.marcuschiu.example.spring.boot.mastercodesnippet.service.util;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.AppMessage;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.LocalChannelState;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.MarkerMessage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Service
public class StateService {

    @Value("${node.id}")
    Integer nodeID;

    @Autowired
    Configuration configuration;

    @Autowired
    MarkerMessageService markerMessageService;

    private Integer numNeighbors;

    public volatile ArrayList<Integer> localState;
    private volatile AtomicInteger currentSnapshotPeriod;
    private volatile HashMap<Integer, LocalChannelState> inProgress;
    private volatile HashMap<Integer, LocalChannelState> done;

    @PostConstruct
    public void StateService() {
        currentSnapshotPeriod = new AtomicInteger(0);
        numNeighbors = configuration.getConfigurationNodeInfos().get(nodeID).getNeighborNodeIDs().size();
        inProgress = new HashMap<>();
        done = new HashMap<>();

        localState = new ArrayList<>();
        for (int i = 0; i < configuration.getNumNodes(); i++) {
            localState.add(0);
        }
    }

    public void processAppMessage(AppMessage appMessage) {
        bufferReceivedAppMessage(appMessage);
        updateLocalState_ReceivedAppMessage(appMessage);
    }

    private void bufferReceivedAppMessage(AppMessage appMessage) {
        Integer messageSnapshotPeriod = appMessage.getSnapshotPeriod();

        // this if statement is needed because the channel where messages arrive are not guaranteed FIFO
        if (messageSnapshotPeriod > currentSnapshotPeriod.get()) {
            System.out.println("ERROR: messageSnapshotPeriod > local current snapshot period");
        } else if (messageSnapshotPeriod < currentSnapshotPeriod.get()) {
            for(LocalChannelState localChannelState : inProgress.values()) {
                if ( messageSnapshotPeriod < localChannelState.getSnapshotPeriod()) {
                    localChannelState.getChannelState().add(appMessage);
                }
            }
        } else {
//            System.out.println("RECEIVED APP MESSAGE: messageSnapshotPeriod == CurrentSnapshotPeriod");
        }
    }

    private void updateLocalState_ReceivedAppMessage(AppMessage appMessage) {

    }

    public void updateLocalState_SendingAppMessage(AppMessage appMessage) {

    }

    public void processMarkerMessage(MarkerMessage markerMessage) {
        if (markerMessage.getSnapshotPeriod() > currentSnapshotPeriod.get()) {
//            System.out.println("RECEIVED - MARKER MESSAGE - from node id: " + markerMessage.getFromNodeID() + " - FIRST");
            recordNewLocalChannelState(false);
            markerMessageService.sendMarkerMessagesToNeighbors(markerMessage.getSnapshotPeriod());
        } else {
//            System.out.println("RECEIVED - MARKER MESSAGE - from node id: " + markerMessage.getFromNodeID() + " - DUPLICATE");
            updateLocalChannelState(markerMessage);
        }
    }

    /**
     * this method call only works for one node for now
     */
    public void selfInitiateSnapshot() {
        recordNewLocalChannelState(true);
        markerMessageService.sendMarkerMessagesToNeighbors(currentSnapshotPeriod.get());
    }

    private void recordNewLocalChannelState(Boolean selfInitiate) {
        Integer csp = currentSnapshotPeriod.incrementAndGet();

        LocalChannelState localChannelState = new LocalChannelState();

        localChannelState.setChannelState(new ArrayList<>());
        localChannelState.setNodeID(nodeID);
        localChannelState.setSnapshotPeriod(csp);

        if (selfInitiate) {
            localChannelState.setNumMarkerMessagesReceived(0);
        } else {
            localChannelState.setNumMarkerMessagesReceived(1);
        }

        localChannelState.setLocalState((ArrayList<Integer>)localState.clone());

        inProgress.put(csp, localChannelState);
    }

    private void updateLocalChannelState(MarkerMessage markerMessage) {
        Integer markerSnapshotPeriod = markerMessage.getSnapshotPeriod();
        LocalChannelState localChannelState = inProgress.get(markerSnapshotPeriod);
        if (localChannelState != null) {
            Integer numReceived = localChannelState.incAndGetNumMarkerMessagesReceived();
            if (numReceived.equals(numNeighbors)) {
                inProgress.remove(markerSnapshotPeriod);
                done.put(markerSnapshotPeriod, localChannelState);
                System.out.println(localChannelState);
                System.out.println("DONE: received all marker messages");
            }
        } else {
            System.out.println("ERROR: received useless marker message");
        }
    }
}
