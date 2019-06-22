package com.marcuschiu.example.spring.boot.mastercodesnippet.service;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.ConfigurationNodeInfo;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.ConvergeCastMessage;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.ConvergeCastState;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.GraphNetworkNode;
import com.marcuschiu.example.spring.boot.mastercodesnippet.model.LocalChannelState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ConvergeCastMessageService {

    // better than synchronized (intrinsic lock)
    // checks the queued threads and gives priority access to the longest waiting one
    private final ReentrantLock reLockConvergeCast = new ReentrantLock(true);
    // TODO maybe move this to EventService?

    @Value("${node.id}")
    Integer nodeID;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Configuration configuration;

    @Autowired
    HaltService haltService;

    @Autowired
    PerpetualSnapshotTakingService perpetualSnapshotTakingService;

    // final variables
    private String parentConvergeCastURL;
    private Integer numChildrenPlusOne;
    private ConvergeCastMessage templateConvergeCastMessage;
    private Integer parentNodeID;

    // variables that change in runtime
    private volatile HashMap<Integer, ConvergeCastState> convergeCastStuff;

    @PostConstruct
    public void convergeCastMessageService() {
        GraphNetworkNode graphNetworkNode = get();
        if (nodeID != 0) {
            parentNodeID = graphNetworkNode.getParentID();
            parentConvergeCastURL = configuration.getConfigurationNodeInfos().get(parentNodeID).getNodeURL() + "/converge-cast/message";
        }
        numChildrenPlusOne = graphNetworkNode.getChildrenIDs().size() + 1;
        convergeCastStuff = new HashMap<>();
        templateConvergeCastMessage = new ConvergeCastMessage();
    }

    @Async
    public void process(ConvergeCastMessage convergeCastMessage) {
        synchronousProcess(convergeCastMessage);
    }

    @Async
    public void processSelfLocalChannelState(Integer snapshotPeriod, LocalChannelState localChannelState) {
        ArrayList<LocalChannelState> localChannelStates = new ArrayList<>();
        localChannelStates.add(localChannelState);

        ConvergeCastMessage convergeCastMessage = new ConvergeCastMessage();
        convergeCastMessage.setLocalChannelStates(localChannelStates);
        convergeCastMessage.setSnapshotPeriod(snapshotPeriod);

        synchronousProcess(convergeCastMessage);
    }

    private void synchronousProcess(ConvergeCastMessage convergeCastMessage) {
        reLockConvergeCast.lock();
        try {
            // process converge-cast message
            Integer snapshotPeriod = convergeCastMessage.getSnapshotPeriod();
            ConvergeCastState convergeCastState = convergeCastStuff.get(snapshotPeriod);
            if (convergeCastState == null) {
                convergeCastState = new ConvergeCastState();
                convergeCastState.setNumConvergeCastMessagesReceived(1);
                convergeCastState.setLocalChannelStates(convergeCastMessage.getLocalChannelStates());
                convergeCastStuff.put(snapshotPeriod, convergeCastState);
            } else {
                convergeCastState.getLocalChannelStates().addAll(convergeCastMessage.getLocalChannelStates());
                convergeCastState.incAndGetNumConvergeCastMessagesReceived();
            }

            // check if all converge-cast messages are received
            if (convergeCastState.getNumConvergeCastMessagesReceived().equals(numChildrenPlusOne)) {
                convergeCastStuff.remove(snapshotPeriod);
                asynchronousSendOrStore(snapshotPeriod, convergeCastState.getLocalChannelStates());
            } else if (convergeCastState.getNumConvergeCastMessagesReceived() > numChildrenPlusOne) {
                System.out.println("ERROR - received more converge cast messages than suppose to");
            }
        } finally {
            reLockConvergeCast.unlock();
        }
    }

    private void asynchronousSendOrStore(Integer snapshotPeriod, ArrayList<LocalChannelState> localChannelStates) {
        new Thread(() -> {
            if (nodeID != 0) {
                sendConvergeCastMessageToParentNode(snapshotPeriod, localChannelStates);
            } else {
                perpetualSnapshotTakingService.receivedAllLocalChannelStates(snapshotPeriod, localChannelStates);
            }
        }).start();
    }

    private void sendConvergeCastMessageToParentNode(Integer snapshotPeriod, ArrayList<LocalChannelState> localChannelStates) {
        templateConvergeCastMessage.setSnapshotPeriod(snapshotPeriod);
        templateConvergeCastMessage.setLocalChannelStates(localChannelStates);

        restTemplate.postForObject(
                parentConvergeCastURL,
                templateConvergeCastMessage,
                String.class);

        System.out.println("SENT CONVERGE-CAST MESSAGE - to parent node id: " + parentNodeID);
    }

    private GraphNetworkNode get() {
        ArrayList<GraphNetworkNode> graphNetworkNodes = new ArrayList<>();

        ArrayList<ConfigurationNodeInfo> configurationNodeInfos = configuration.getConfigurationNodeInfos();
        for (ConfigurationNodeInfo cni : configurationNodeInfos) {
            GraphNetworkNode graphNetworkNode = new GraphNetworkNode();

            graphNetworkNode.nodeID = cni.getNodeID();
            graphNetworkNode.neighborIDs = cni.getNeighborNodeIDs();
            graphNetworkNode.neighborGraphNetworkNodes = new ArrayList<>();
            graphNetworkNode.parentID = null;
            graphNetworkNode.childrenIDs = new ArrayList<>();

            graphNetworkNodes.add(graphNetworkNode);
        }

        // connect them
        for (GraphNetworkNode n : graphNetworkNodes) {
            for (Integer neighborID : n.getNeighborIDs()) {
                n.neighborGraphNetworkNodes.add(graphNetworkNodes.get(neighborID));
            }
        }

        LinkedList<GraphNetworkNode> queue = new LinkedList<>();
        queue.add(graphNetworkNodes.get(0));
        graphNetworkNodes.get(0).parentID = Integer.MAX_VALUE;
        do {
            GraphNetworkNode n = queue.pop();

            for (GraphNetworkNode neighborGraphNetworkNode : n.getNeighborGraphNetworkNodes()) {
                if (neighborGraphNetworkNode.parentID == null) {
                    neighborGraphNetworkNode.parentID = n.nodeID;
                    n.childrenIDs.add(neighborGraphNetworkNode.nodeID);
                    queue.add(neighborGraphNetworkNode);
                }
            }
        } while(queue.size() > 0);

        return graphNetworkNodes.get(nodeID);
    }
}
