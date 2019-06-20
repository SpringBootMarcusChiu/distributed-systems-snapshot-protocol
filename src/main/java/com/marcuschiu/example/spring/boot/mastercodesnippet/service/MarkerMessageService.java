package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.MarkerMessage;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.MarkerMessageResponse;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.MarkerMessageResponseLocal;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Service
public class MarkerMessageService {

    @Value("${node.id}")
    Integer nodeID;

    @Autowired
    Configuration configuration;

    @Autowired
    RestTemplate restTemplate;

    public volatile AtomicInteger snapshotPeriod;

    public MarkerMessageService() {
        snapshotPeriod = new AtomicInteger(0);
    }

    public MarkerMessageResponse acceptMessage(MarkerMessage markerMessage) {
        MarkerMessageResponse markerMessageResponse;

        if (markerMessage.getSnapshotPeriod() > snapshotPeriod.get()) {
            System.out.println("received marker message from: " + markerMessage.getFromNodeID() + " - first");
            snapshotPeriod.incrementAndGet();
            takeSnapshot();
            markerMessageResponse = sendMarkerMessagesToNeighbors(markerMessage);
            markerMessageResponse.setIsDuplicate(false);
            return markerMessageResponse;
        } else {
            System.out.println("received marker message from: " + markerMessage.getFromNodeID() + " - duplicate");
            markerMessageResponse = new MarkerMessageResponse();
            markerMessageResponse.setIsDuplicate(true);
            return markerMessageResponse;
        }
    }

    private void takeSnapshot() {
//        System.out.println("taking snapshot");
    }

    private MarkerMessageResponse sendMarkerMessagesToNeighbors(MarkerMessage markerMessage) {
        MarkerMessageResponse markerMessageResponse = new MarkerMessageResponse();
        markerMessageResponse.setFromNodeID(nodeID);
        markerMessageResponse.setMarkerMessageResponseLocals(new ArrayList<>());
        markerMessageResponse.setSnapshotPeriod(markerMessage.getSnapshotPeriod());

        MarkerMessageResponseLocal markerMessageResponseLocal = new MarkerMessageResponseLocal();
        markerMessageResponseLocal.setNodeID(nodeID);
        markerMessageResponse.getMarkerMessageResponseLocals().add(markerMessageResponseLocal);

        ArrayList<Integer> neighbors = configuration.getConfigurationNodeInfos().get(nodeID).getNeighbors();

        for (Integer neighborID : neighbors) {
            if (!neighborID.equals(markerMessage.getFromNodeID())) {
                String url = configuration.getConfigurationNodeInfos().get(neighborID).getNodeURL() + "/marker";

                MarkerMessage markerMessageToNeighbor = new MarkerMessage();
                markerMessageToNeighbor.setFromNodeID(nodeID);
                markerMessageToNeighbor.setSnapshotPeriod(markerMessage.getSnapshotPeriod());

                MarkerMessageResponse markerMessageResponseNeighbor = restTemplate.postForObject(url, markerMessageToNeighbor, MarkerMessageResponse.class);

                if (!markerMessageResponseNeighbor.getIsDuplicate()) {
                    for (MarkerMessageResponseLocal mmrl : markerMessageResponseNeighbor.getMarkerMessageResponseLocals()) {
                        markerMessageResponse.getMarkerMessageResponseLocals().add(mmrl);
                    }
                }

                System.out.println("sent marker message to node: " + neighborID);
            }
        }

        return markerMessageResponse;
    }
}
