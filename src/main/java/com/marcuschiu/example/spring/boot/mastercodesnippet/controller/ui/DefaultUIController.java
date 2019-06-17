package com.marcuschiu.example.spring.boot.mastercodesnippet.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Controller annotation will tell SpringBootWebApplication to include this class
 */
@Controller
@RequestMapping("ui/default")
public class DefaultUIController {

    /**
     * localhost:8080/ui/default/
     * @return
     */
    @RequestMapping("/")
    public String home() { return "home"; }

    /**
     * localhost:8080/ui/default/home
     * @return
     */
    @RequestMapping("/home")
    public String home1() { return "home"; }

    /**
     * localhost:8080/ui/default/about
     * @return
     */
    @RequestMapping("/about")
    public String about() { return "about"; }

    /**
     * localhost:8080/ui/default/hello
     * http://localhost:8080/ui/default/hello?name=Marcus Chiu
     * http://localhost:8080/ui/default/hello?name=Marcus%20Chiu
     * @param name
     * @param model
     * @return
     */
    @RequestMapping("/hello")
    public String getHello(@RequestParam(value="name", required = false, defaultValue = "Default Value") String name, Model model) {
        model.addAttribute("name", name);
        return "hello";
    }
}
