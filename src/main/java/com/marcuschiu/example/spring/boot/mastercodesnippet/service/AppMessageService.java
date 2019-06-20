package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.controller.model.AppMessage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Data
@Service
public class AppMessageService {

    @Autowired
    Configuration configuration;

    @Autowired
    MAPProtocolService mapProtocolService;

    // better than synchronized (intrinsic lock)
    // checks the queued threads and gives priority access to the longest waiting one
    private final ReentrantLock reLock = new ReentrantLock(true);

    @Async
    public void acceptMessage(AppMessage applicationMessage) throws InterruptedException {
        reLock.lock();
        try {
            if (!mapProtocolService.getIsActive().get() && mapProtocolService.getNumMessagesSent().get() < configuration.getMaxNumber()) {
                mapProtocolService.getIsActive().set(true);
                mapProtocolService.startMAPProtocol();
            }
        } finally {
            reLock.unlock();
        }
    }
}
