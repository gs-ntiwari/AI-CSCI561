package com.HW1;

import java.util.List;

public class Input {
    Coordinate landingSite;
    int maxElevation;
    List<Coordinate> targets;
    int[][] elevationMap;
    String algo;
    List<String> output;

    Input(Coordinate landingSite, int maxElevation, List<Coordinate> targets, int[][] elevationMap, String algo) {
        this.elevationMap = elevationMap;
        this.targets = targets;
        this.maxElevation = maxElevation;
        this.landingSite = landingSite;
        this.algo = algo;
    }

    Input(Coordinate landingSite, int maxElevation, List<Coordinate> targets, int[][] elevationMap, String algo, List<String> output) {
        this.elevationMap = elevationMap;
        this.targets = targets;
        this.maxElevation = maxElevation;
        this.landingSite = landingSite;
        this.algo = algo;
        this.output = output;
    }
}
