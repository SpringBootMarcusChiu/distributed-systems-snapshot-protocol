package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.MarkerMessage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Data
@Service
public class MarkerMessageService {

    @Value("${node.id}")
    Integer nodeID;

    @Autowired
    Configuration configuration;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StateService stateService;

    private ArrayList<String> neighborNodeMarkerURLs;
    private MarkerMessage markerMessageToNeighbor;

    @PostConstruct
    public void markerMessageService() {
        neighborNodeMarkerURLs = new ArrayList<>();
        ArrayList<Integer> neighborNodeIDs = configuration.getConfigurationNodeInfos().get(nodeID).getNeighborNodeIDs();
        for (Integer neighborID : neighborNodeIDs) {
            String url = configuration.getConfigurationNodeInfos().get(neighborID).getNodeURL() + "/marker/message";
            neighborNodeMarkerURLs.add(url);
        }

        markerMessageToNeighbor = new MarkerMessage();
        markerMessageToNeighbor.setFromNodeID(nodeID);
    }

    public void process(MarkerMessage markerMessage) {
        stateService.processMarkerMessage(markerMessage);
    }

    public void sendMarkerMessagesToNeighbors(Integer snapshotPeriod, Boolean shutdown) {
        markerMessageToNeighbor.setSnapshotPeriod(snapshotPeriod);
        markerMessageToNeighbor.setShutdown(shutdown);

        for (String neighborNodeMarkerURL : neighborNodeMarkerURLs) {
            restTemplate.postForObject(neighborNodeMarkerURL, markerMessageToNeighbor, String.class);
        }
    }
}
