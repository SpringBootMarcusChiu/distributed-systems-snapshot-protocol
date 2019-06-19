package com.marcuschiu.example.spring.boot.mastercodesnippet.controller.api;

import com.marcuschiu.example.spring.boot.mastercodesnippet.service.MAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/application")
public class ApplicationAPIController {

    @Autowired
    private MAPService mapService;

    @GetMapping("message")
    public @ResponseBody String receiveMessage() throws InterruptedException {
        this.mapService.start();
        return "message received";
    }

    @GetMapping("reset")
    public @ResponseBody String resetMAPProtocol() {
        this.mapService.reset();
        return "map protocol reset";
    }
}
