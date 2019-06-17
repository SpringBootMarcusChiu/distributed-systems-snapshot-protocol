package com.marcuschiu.example.spring.boot.mastercodesnippet.controller.header;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("header")
public class HeaderController {

    /**
     * go to http://localhost:8080/header/user-agent-1
     * @return String
     */
    @GetMapping("/user-agent-1")
    public String getUserAgent1(@RequestHeader(value="User-Agent", defaultValue="jesus") String userAgent) {
        return userAgent;
    }

    /**
     * go to http://localhost:8080/header/user-agent-2
     * @return String
     */
    @GetMapping("/user-agent-2")
    public String getUserAgent2(HttpServletRequest request) {
        return request.getHeader("user-agent");
    }
}
