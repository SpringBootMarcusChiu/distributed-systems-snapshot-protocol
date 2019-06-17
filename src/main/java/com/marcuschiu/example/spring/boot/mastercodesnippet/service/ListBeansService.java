package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ListBeansService {

    @Autowired
    ApplicationContext applicationContext;

    public List printBeans() {
        return Arrays.asList(applicationContext.getBeanDefinitionNames());
    }
}
