package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.AppMessage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Service
public class MAPProtocolService {

    @Value("${node.id}")
    Integer nodeID;

    @Autowired
    Configuration configuration;

    @Autowired
    RestTemplate restTemplate;

    public volatile AtomicBoolean isActive;
    public volatile AtomicInteger numMessagesSent;

    public MAPProtocolService() {
        isActive = new AtomicBoolean(false);
        numMessagesSent = new AtomicInteger(0);
    }
    /**
     * @Async needs method and does not work when method is called within class
     * @throws InterruptedException
     */
    public void startMAPProtocol() throws InterruptedException {
        Random rand = new Random();
        int difference = configuration.getMaxPerActive() - configuration.getMinPerActive();
        int randPerActive = configuration.getMinPerActive() + rand.nextInt(difference);
        int minSendDelay  = configuration.getMinSendDelay();

        ArrayList<Integer> neighbors = configuration.getConfigurationNodeInfos().get(nodeID).getNeighbors();
        int numNeighbors = neighbors.size();

        for (int i = 0; i <= randPerActive; i++) {
            if (numMessagesSent.get() < configuration.getMaxNumber()) {
                Thread.sleep(minSendDelay);
                int randomNeighborID = neighbors.get(rand.nextInt(numNeighbors));

                String url = configuration.getConfigurationNodeInfos().get(randomNeighborID).getNodeURL() + "/app";

                AppMessage appMessage = new AppMessage();
                appMessage.setFromNodeID(nodeID);
                HttpEntity<AppMessage> request = new HttpEntity<>(appMessage);

                restTemplate.postForEntity(url, request, String.class);

                System.out.println("sending appMessage to node:" + randomNeighborID);

                numMessagesSent.getAndIncrement();
            } else {
                break;
            }
        }

        isActive.set(false);
    }
}
