package com.marcuschiu.example.spring.boot.mastercodesnippet.controller;

import com.marcuschiu.example.spring.boot.mastercodesnippet.model.ConvergeCastMessage;
import com.marcuschiu.example.spring.boot.mastercodesnippet.service.ConvergeCastMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/converge-cast")
public class ConvergeCastMessageController {

    @Autowired
    ConvergeCastMessageService convergeCastMessageService;

    @PostMapping("/message")
    public @ResponseBody String message(@RequestBody ConvergeCastMessage convergeCastMessage) {
        convergeCastMessageService.process(convergeCastMessage);
        return "received";
    }
}
