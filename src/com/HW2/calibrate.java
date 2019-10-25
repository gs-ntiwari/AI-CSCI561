package com.HW2;

import java.util.Set;

public class calibrate {

    public static void main(String[] args)
    {
        float error=0;
        long startTime= System.currentTimeMillis();
        Set<Coordinate> initialBlackPositions =Utility.getInitialPositions(CellType.Black);
        Set<Coordinate> initialWhitePositions =Utility.getInitialPositions(CellType.White);
        GameState initState = new GameState(initialWhitePositions, initialBlackPositions, CellType.Black, null);
        initState.generateBranches(1, CellType.Black);
        long totalTime = System.currentTimeMillis()-startTime;
        error+=(totalTime-50)/50.0;
        System.out.println("Time taken for generating "+totalTime);
        startTime= System.currentTimeMillis();
        initState = new GameState(initialWhitePositions, initialBlackPositions, CellType.Black, null);
        initState.generateBranches(3, CellType.Black);
        totalTime = System.currentTimeMillis()-startTime;
        error+=(totalTime-85)/85.0;
        System.out.println("Time taken for generating "+totalTime);
        startTime= System.currentTimeMillis();
        initState = new GameState(initialWhitePositions, initialBlackPositions, CellType.Black, null);
        initState.generateBranches(5, CellType.Black);
        totalTime = System.currentTimeMillis()-startTime;
        error+=(totalTime-1500)/1500.0;
        System.out.println("Time taken for generating "+totalTime);
        System.out.println(error);

    }
}
