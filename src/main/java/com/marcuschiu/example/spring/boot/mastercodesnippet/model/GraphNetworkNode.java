package com.marcuschiu.example.spring.boot.mastercodesnippet.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class GraphNetworkNode {
    public Integer nodeID;
    public ArrayList<GraphNetworkNode> neighborGraphNetworkNodes;
    public ArrayList<Integer> neighborIDs;

    public Integer parentID;
    public ArrayList<Integer> childrenIDs;
}
