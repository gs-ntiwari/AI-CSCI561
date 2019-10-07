package com.HW2;

import java.io.IOException;

public class homework {
    public static void main(String[] args)
    {
        try {
            Input input=Utility.readFile("/Users/nishatiwari/CSCI561/src/com.HW2/input.txt");
            GameState startingState=input.getInitState();
            startingState.generateBranches(4, CellType.White);
            System.out.println(startingState.getColor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
