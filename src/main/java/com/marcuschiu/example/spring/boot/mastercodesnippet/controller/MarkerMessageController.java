package com.marcuschiu.example.spring.boot.mastercodesnippet.controller;

import com.marcuschiu.example.spring.boot.mastercodesnippet.model.MarkerMessage;
import com.marcuschiu.example.spring.boot.mastercodesnippet.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/marker")
public class MarkerMessageController {

    @Autowired
    EventService eventService;

    @PostMapping("/message")
    public void messageControl(@RequestBody MarkerMessage markerMessage) {
        eventService.process(markerMessage);
    }
}
