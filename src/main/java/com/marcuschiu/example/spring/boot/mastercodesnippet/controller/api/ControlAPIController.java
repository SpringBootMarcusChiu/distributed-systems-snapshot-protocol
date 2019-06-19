package com.marcuschiu.example.spring.boot.mastercodesnippet.controller.api;

import com.marcuschiu.example.spring.boot.mastercodesnippet.controller.api.model.Snapshots;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/control")
public class ControlAPIController {

    @GetMapping("marker-message")
    public @ResponseBody String markerMessage() {
        return "marker message received";
    }

    @GetMapping("local-snapshots")
    public @ResponseBody Snapshots localSnapshots(Snapshots snapshots) {
        return new Snapshots();
    }
}
