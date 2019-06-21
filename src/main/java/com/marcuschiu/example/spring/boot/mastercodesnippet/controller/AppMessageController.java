package com.marcuschiu.example.spring.boot.mastercodesnippet.controller;

import com.marcuschiu.example.spring.boot.mastercodesnippet.model.AppMessage;
import com.marcuschiu.example.spring.boot.mastercodesnippet.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/app")
public class AppMessageController {

    @Autowired
    EventService eventService;

    @PostMapping("/message")
    public @ResponseBody String message(@RequestBody AppMessage appMessage) {
        eventService.process(appMessage);
        return "received";
    }
}
