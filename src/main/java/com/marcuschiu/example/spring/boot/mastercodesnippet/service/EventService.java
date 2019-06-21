package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.model.AppMessage;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.MarkerMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Service
public class EventService {

    // better than synchronized (intrinsic lock)
    // checks the queued threads and gives priority access to the longest waiting one
    private final ReentrantLock reLock = new ReentrantLock(true);

    @Lazy
    @Autowired
    AppMessageService appMessageService;

    @Lazy
    @Autowired
    MapProtocolService mapProtocolService;

    @Lazy
    @Autowired
    MarkerMessageService markerMessageService;

    @Autowired
    StateService stateService;

    @Async
    public void sendAppMessage(Integer toNodeID) {
        reLock.lock();
        try {
            appMessageService.sendAppMessage(toNodeID);
        } finally {
            reLock.unlock();
        }
    }

    @Async
    public void process(AppMessage appMessage) {
        reLock.lock();
        try {
            appMessageService.processAppMessage(appMessage);
            mapProtocolService.startMAPProtocol();
        } finally {
            reLock.unlock();
        }
    }

    @Async
    public void process(MarkerMessage markerMessage) {
        reLock.lock();
        try {
            markerMessageService.process(markerMessage);
        } finally {
            reLock.unlock();
        }
    }

    @Async
    public void selfInitiateSnapshot() {
        stateService.selfInitiateSnapshot();
    }
}
