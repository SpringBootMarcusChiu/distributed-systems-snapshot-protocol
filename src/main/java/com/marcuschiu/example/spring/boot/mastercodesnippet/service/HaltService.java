package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class HaltService {
    @Autowired
    private ApplicationContext appContext;

    public void initiateShutdown(){
        SpringApplication.exit(appContext, () -> 0);
    }
}
