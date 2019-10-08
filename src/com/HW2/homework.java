package com.HW2;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class homework {
    public static void main(String[] args)
    {
        try {
            long startTime=System.currentTimeMillis();
            Input input=Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW2/input.txt");
            GameState startingState=input.getInitState();
            Set<Coordinate> whitePositions = new HashSet<>(startingState.getWhitePositions());
            Set<Coordinate> blackPositions = new HashSet<>(startingState.getBlackPositions());
            startingState.generateBranches(3, CellType.White, whitePositions, blackPositions);
            System.out.println("time taken "+String.valueOf(System.currentTimeMillis()-startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
