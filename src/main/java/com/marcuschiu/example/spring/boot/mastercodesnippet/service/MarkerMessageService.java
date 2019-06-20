package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.controller.model.MarkerMessage;
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

    public void acceptMessage(MarkerMessage markerMessage) {
        takeSnapshot();
        sendMarkerMessagesToNeighbors(markerMessage);
    }

    private void takeSnapshot() {
        System.out.println("taking snapshot");
    }

    private void sendMarkerMessagesToNeighbors(MarkerMessage markerMessage) {
        ArrayList<Integer> neighbors = configuration.getConfigurationNodeInfos().get(nodeID).getNeighbors();

        for (Integer neighborID : neighbors) {
            if (!neighborID.equals(markerMessage.getFromNodeID())) {
                String url = configuration.getConfigurationNodeInfos().get(neighborID).getNodeURL() + "/marker";

                MarkerMessage markerMessageToNeighbor = new MarkerMessage();
                markerMessage.setFromNodeID(nodeID);
                markerMessage.setSnapshotPeriod(markerMessage.getSnapshotPeriod());

                HttpEntity<MarkerMessage> request = new HttpEntity<>(markerMessageToNeighbor);

                restTemplate.postForEntity(url, request, String.class);

                System.out.println("sent marker message to node: " + neighborID);
            }
        }
    }
}
