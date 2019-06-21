package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Service
public class MapProtocolService {

    // better than synchronized (intrinsic lock)
    // checks the queued threads and gives priority access to the longest waiting one
    private final ReentrantLock reLock = new ReentrantLock(true);

    @Value("${node.id}")
    Integer nodeID;

    @Autowired
    EventService eventService;

    @Autowired
    Configuration configuration;

    private Integer maxNumber;
    private Integer minPerActive;
    private Integer difference;
    private Integer minSendDelay;
    private ArrayList<Integer> neighborNodeIDs;
    private Random rand;

    public volatile AtomicBoolean isActive;
    public volatile AtomicInteger numMessagesQueuedToBeSent;

    @PostConstruct
    public void MapProtocolService() {
        isActive = new AtomicBoolean(false);
        numMessagesQueuedToBeSent = new AtomicInteger(0);
        maxNumber = configuration.getMaxNumber();

        minPerActive = configuration.getMinPerActive();
        difference = configuration.getMaxPerActive() - configuration.getMinPerActive();
        minSendDelay  = configuration.getMinSendDelay();
        neighborNodeIDs = configuration.getConfigurationNodeInfos().get(nodeID).getNeighborNodeIDs();
        rand = new Random();
    }

    public void startMAPProtocol() {
        reLock.lock();
        try {
            if (!isActive.get() && numMessagesQueuedToBeSent.get() < maxNumber) {
                isActive.set(true);
                asynchronousStart();
            }
        } finally {
            reLock.unlock();
        }
    }

    private void asynchronousStart() {
        new Thread(() -> {
            int randPerActive = minPerActive + rand.nextInt(difference);

            int numNeighbors = neighborNodeIDs.size();

            for (int i = 0; i <= randPerActive; i++) {
                if (numMessagesQueuedToBeSent.get() < maxNumber) {
                    numMessagesQueuedToBeSent.incrementAndGet();

                    try {
                        Thread.sleep(minSendDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Integer randomNeighborID = neighborNodeIDs.get(rand.nextInt(numNeighbors));
                    eventService.sendAppMessage(randomNeighborID);
                } else {
                    break;
                }
            }

            isActive.set(false);
        }).start();
    }
}
