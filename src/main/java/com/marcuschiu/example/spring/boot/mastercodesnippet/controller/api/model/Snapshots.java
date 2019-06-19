package com.marcuschiu.example.spring.boot.mastercodesnippet.controller.api.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Snapshots {
    ArrayList<LocalSnapshot> localSnapshots;
}
