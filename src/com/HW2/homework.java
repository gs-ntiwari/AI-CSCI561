package com.HW2;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class homework {
    public static void main(String[] args) throws ClassNotFoundException {
        try {
            //long startTime=System.currentTimeMillis();
            Input input=Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW2/input.txt");
            //int currentMove=Utility.updateMoveCount();
            int depth;
            GameState startingState=input.getInitState();
            if(input.time<20)
                depth=1;
            else
                depth=5;
            if(input.isSinglePlayer)
                startingState.generateBranches(1, startingState.getColor());
            else {
                startingState.generateBranches(depth, startingState.getColor());
            }
            List<Integer> indices =startingState.tieBreaker.get(startingState.alpha);
            int bestIndex;
            if(indices==null ||indices.size()>1)
                bestIndex=Utility.filterOutBestNodeBasedOnEvalFunction(startingState,indices);
            else
                bestIndex=startingState.bestNode;

            //Utility.writeOutput(startingState.getBranches().get(indices.size()>1?indices.get((new Random()).nextInt(indices.size())): startingState.bestNode));
            Utility.writeOutput(startingState.getBranches().get(bestIndex));
            //System.out.println("time taken "+String.valueOf(System.currentTimeMillis()-startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void gamePlay(int depth)
    {
        try {
            long startTime=System.currentTimeMillis();
            Input input=Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW2/input.txt");
            GameState startingState=input.getInitState();
            Map<Integer, Integer> branchesStats= new HashMap<>();
            if(input.time<15)
                depth=1;
            else
                depth=5;
            if(input.isSinglePlayer)
                startingState.generateBranches(1, startingState.getColor());
            else {
                startingState.generateBranches(depth, startingState.getColor());
            }
            // Since our player is a MAX player, hence alpha indicates the max possible value.
            List<Integer> indices =startingState.tieBreaker.get(startingState.alpha);
            int bestIndex=-1;
            if(indices==null ||indices.size()>1)
                bestIndex=Utility.filterOutBestNodeBasedOnEvalFunction(startingState,indices);
            else
                bestIndex=startingState.bestNode;

            //Utility.writeOutput(startingState.getBranches().get(indices.size()>1?indices.get((new Random()).nextInt(indices.size())): startingState.bestNode));
            Utility.writeOutput(startingState.getBranches().get(bestIndex));
            //System.out.println(indices.size());
            //System.out.println("Pruned Nodes::"+ startingState.prunedNodes);
            //System.out.println(branchesStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
