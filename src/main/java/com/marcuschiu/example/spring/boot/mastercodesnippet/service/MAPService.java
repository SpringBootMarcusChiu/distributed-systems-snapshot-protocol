package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.ConfigurationNodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MAPService {

    @Value("${node.id}")
    private Integer nodeID;

    @Autowired
    private Configuration configuration;

    @Autowired
    private RestTemplate restTemplate;

    private volatile AtomicBoolean isActive = new AtomicBoolean(false);
    private volatile AtomicInteger numMessagesSent = new AtomicInteger(0);

    @Async
    public synchronized void start() throws InterruptedException {
        if (!this.isActive.get() && this.numMessagesSent.get() < this.configuration.getMaxNumber()) {
            this.isActive.set(true);
            startHelper();
        }
    }

    private void startHelper() throws InterruptedException {
        Random rand = new Random();
        int difference = this.configuration.getMaxPerActive() - this.configuration.getMinPerActive();
        int randPerActive = this.configuration.getMinPerActive() + rand.nextInt(difference);
        int minSendDelay  = this.configuration.getMinSendDelay();

        ArrayList<Integer> neighbors = this.configuration.getConfigurationNodeInfos().get(nodeID).getNeighbors();
        int numNeighbors = neighbors.size();

        for (int i = 0; i <= randPerActive; i++) {
            if (this.numMessagesSent.get() < this.configuration.getMaxNumber()) {
                Thread.sleep(minSendDelay);
                int randomNeighborID = neighbors.get(rand.nextInt(numNeighbors));

                String url = this.configuration.getConfigurationNodeInfos().get(randomNeighborID).getMessageURL();
                restTemplate.getForObject(url, String.class);

                System.out.println("sending message to node:" + randomNeighborID);

                this.numMessagesSent.getAndIncrement();
            } else {
                break;
            }
        }

        this.isActive.set(false);
    }

    public void reset() {
        this.isActive.set(false);
        this.numMessagesSent.set(0);
        System.out.println("node reset");

        if (this.nodeID == 0) {
            for (ConfigurationNodeInfo cni : this.configuration.getConfigurationNodeInfos()) {
                if (!cni.getNodeID().equals("0")) {
                    String url = cni.getNodeURL() + "/api/application/reset";
                    String result = restTemplate.getForObject(url, String.class);
                }
            }
        }
    }

    public Boolean isActive() {
        return this.isActive.get();
    }
}
