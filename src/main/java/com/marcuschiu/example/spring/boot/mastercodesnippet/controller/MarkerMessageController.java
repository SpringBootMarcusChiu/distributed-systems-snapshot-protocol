package com.marcuschiu.example.spring.boot.mastercodesnippet.controller;

import com.marcuschiu.example.spring.boot.mastercodesnippet.controller.model.MarkerMessage;
import com.marcuschiu.example.spring.boot.mastercodesnippet.service.MarkerMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MarkerMessageController {

    @Autowired
    private MarkerMessageService markerMessageService;

    @PostMapping("/marker")
    public @ResponseBody String messageControl(@RequestBody MarkerMessage markerMessage) {
        markerMessageService.acceptMessage(markerMessage);
        return "message received";
    }
}
