package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SnapshotService {

    @Value("${node.id}")
    Integer nodeID;

    @Autowired
    Configuration configuration;

    private Boolean started = false;

    @Async
    public synchronized void start() throws InterruptedException {
        if (!this.started) {
            this.started = true;

            while(true) {
                Thread.sleep(200);
                System.out.println("snapshot service");
            }
        }
    }
}
