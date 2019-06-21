package com.marcuschiu.example.spring.boot.mastercodesnippet.service.util;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.ConfigurationNodeInfo;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.AppMessage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Service
public class AppMessageService {

    @Value("${node.id}")
    Integer nodeID;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Configuration configuration;

    @Autowired
    StateService stateService;

    private volatile AtomicInteger numAppMessagesSent;
    private volatile AppMessage templateAppMessage;
    private volatile HashMap<Integer, String> nodeAppURLs;

    @PostConstruct
    public void AppMessageService() {
        numAppMessagesSent = new AtomicInteger(0);

        templateAppMessage = new AppMessage();
        templateAppMessage.setFromNodeID(nodeID);

        nodeAppURLs = new HashMap<>();
        for (ConfigurationNodeInfo configurationNodeInfo : configuration.getConfigurationNodeInfos()) {
            nodeAppURLs.put(
                    configurationNodeInfo.getNodeID(),
                    configurationNodeInfo.getNodeURL() + "/app/message"
            );
        }
    }

    public void processAppMessage(AppMessage appMessage) {
        stateService.processAppMessage(appMessage);
    }

    public void sendAppMessage(Integer toNodeID) {
        templateAppMessage.setSnapshotPeriod(stateService.getCurrentSnapshotPeriod().get());

        restTemplate.postForObject(
                nodeAppURLs.get(toNodeID),
                templateAppMessage,
                String.class);

        numAppMessagesSent.incrementAndGet();

        stateService.updateLocalState_SendingAppMessage(templateAppMessage);
        System.out.println("SENT APP MESSAGE - to node id: " + toNodeID);
    }
}
