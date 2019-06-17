package com.marcuschiu.example.spring.boot.mastercodesnippet.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/application")
public class ApplicationAPIController {

    @GetMapping("message")
    public @ResponseBody String receiveMessage() throws InterruptedException {
        incrementCounter();
        return "done";
    }

    private synchronized void incrementCounter() throws InterruptedException {
        Thread.sleep(5000);
    }
}
