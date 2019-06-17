package com.marcuschiu.example.spring.boot.mastercodesnippet.controller.api;

import com.marcuschiu.example.spring.boot.mastercodesnippet.service.ListBeansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Controller annotation will tell SpringBootWebApplication to include this class
 */
@Controller
@RequestMapping("api/sandbox")
public class SandboxAPIController {

    @Autowired
    ListBeansService listBeansService;

    @GetMapping("list-beans")
    public @ResponseBody List listBeans() {
        return listBeansService.printBeans();
    }
}
