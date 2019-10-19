package com.HW2;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class homework {
    public static void main(String[] args)
    {
        try {
            long startTime=System.currentTimeMillis();
            Input input=Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW2/input.txt");
            GameState startingState=input.getInitState();
            Map<Coordinate, Coordinate> gamepath= new HashMap<>();
            startingState.generateBranches(2, startingState.getColor(), gamepath);
            Utility.writeOutput(startingState.getBranches().get(startingState.bestNode));
            System.out.println("time taken "+String.valueOf(System.currentTimeMillis()-startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void gamePlay()
    {
        try {
            long startTime=System.currentTimeMillis();
            Input input=Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW2/input.txt");
            GameState startingState=input.getInitState();
            Map<Coordinate, Coordinate> gamepath= new HashMap<>();
            startingState.generateBranches(2, startingState.getColor(), gamepath);
            // Since our player is a MAX player, hence alpha indicates the max possible value.
            List<Integer> indices =startingState.tieBreaker.get(startingState.alpha);
            Utility.writeOutput(startingState.getBranches().get(indices.size()>1?indices.get((new Random()).nextInt(indices.size())): startingState.bestNode));
            System.out.println("time taken "+String.valueOf(System.currentTimeMillis()-startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
