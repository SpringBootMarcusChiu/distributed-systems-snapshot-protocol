package com.marcuschiu.example.spring.boot.mastercodesnippet.service.util.mst;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Node {
    Integer nodeID;
    ArrayList<Node> neighborNodes;
    ArrayList<Integer> neighborIDs;

    Integer parentID;
    ArrayList<Integer> childrenIDs;
}
