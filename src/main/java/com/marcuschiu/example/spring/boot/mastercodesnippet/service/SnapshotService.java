package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SnapshotService {

    @Value("${node.id}")
    private Integer nodeID;

    @Autowired
    private Configuration configuration;

    private Boolean started = false;
    private volatile AtomicInteger snapshotPeriod = new AtomicInteger(0);

    @Async
    public synchronized void takeSnapshot() throws InterruptedException {
        if (!this.started) {
            this.started = true;

            while(true) {
                Thread.sleep(this.configuration.getSnapshotDelay());
                System.out.println("snapshot service");
            }
        }
    }

    @Async
    public synchronized void markerMessageReceived() throws InterruptedException {
        if (!this.started) {
            this.started = true;

            while(true) {
                Thread.sleep(this.configuration.getSnapshotDelay());
                System.out.println("snapshot service");
            }
        }
    }

    @Async
    public synchronized void localSnapshotReceived() {

    }
}
