package com.marcuschiu.example.spring.boot.mastercodesnippet.service.util.mst;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.ConfigurationNodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;

@Service
public class GraphNetworkService {

    Integer parentNodeID;
    ArrayList<Integer> childrenNodeIDs;
    Boolean isLeaf;

    @Value("${node.id}")
    Integer nodeID;

    @Autowired
    Configuration configuration;

    @PostConstruct
    public void GraphNetworkService() {
        ArrayList<Node> nodes = generate();
        Node n = nodes.get(nodeID);
        parentNodeID = n.getParentID();
        childrenNodeIDs = n.getChildrenIDs();
        isLeaf = childrenNodeIDs.size() == 0;
    }

    private ArrayList<Node> generate() {
        ArrayList<Node> nodes = new ArrayList<>();

        ArrayList<ConfigurationNodeInfo> configurationNodeInfos = configuration.getConfigurationNodeInfos();
        for (ConfigurationNodeInfo cni : configurationNodeInfos) {
            Node node = new Node();

            node.nodeID = cni.getNodeID();
            node.neighborIDs = cni.getNeighborNodeIDs();
            node.neighborNodes = new ArrayList<>();
            node.parentID = null;
            node.childrenIDs = new ArrayList<>();

            nodes.add(node);
        }

        // connect them
        for (Node n : nodes) {
            for (Integer neighborID : n.getNeighborIDs()) {
                n.neighborNodes.add(nodes.get(neighborID));
            }
        }

        LinkedList<Node> queue = new LinkedList<>();
        queue.add(nodes.get(0));
        nodes.get(0).parentID = Integer.MAX_VALUE;
        do {
            Node n = queue.pop();

            for (Node neighborNode : n.getNeighborNodes()) {
                if (neighborNode.parentID == null) {
                    neighborNode.parentID = n.nodeID;
                    n.childrenIDs.add(neighborNode.nodeID);
                    queue.add(neighborNode);
                }
            }
        } while(queue.size() > 0);

        return nodes;
    }
}
