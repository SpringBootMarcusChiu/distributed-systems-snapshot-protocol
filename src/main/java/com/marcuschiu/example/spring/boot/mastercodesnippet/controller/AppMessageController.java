package com.marcuschiu.example.spring.boot.mastercodesnippet.controller;

import com.marcuschiu.example.spring.boot.mastercodesnippet.controller.model.AppMessage;
import com.marcuschiu.example.spring.boot.mastercodesnippet.service.AppMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AppMessageController {

    @Autowired
    AppMessageService appMessageService;

    @PostMapping("/app")
    public @ResponseBody String message(@RequestBody AppMessage appMessage) throws InterruptedException {
        appMessageService.acceptMessage(appMessage);
        return "appMessage received";
    }
}
