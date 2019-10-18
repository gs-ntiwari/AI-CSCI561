package com.HW2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class homework {
    public static void main(String[] args)
    {
        try {
            long startTime=System.currentTimeMillis();
            Input input=Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW2/test2.txt");
            GameState startingState=input.getInitState();
            Map<Coordinate, Coordinate> gamepath= new HashMap<>();
            startingState.generateBranches(1, startingState.getColor(), gamepath);
            Utility.writeOutput(startingState.getBranches().get(startingState.bestNode));
            System.out.println("time taken "+String.valueOf(System.currentTimeMillis()-startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
