package com.HW2;

import java.io.IOException;
import java.util.List;


public class homework {
    public static void main(String[] args) throws ClassNotFoundException {
        try {
            //long startTime=System.currentTimeMillis();
            Input input=Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW2/multipleJumps.txt");
            /*int currentMove=Utility.updateMoveCount();
            int depth;
            if(currentMove<=30)
                depth=1;
            else if(currentMove<=50)
                depth=3;
            else
                depth=2;*/
            GameState startingState=input.getInitState();
            if(input.isSinglePlayer)
                startingState.generateBranches(1, startingState.getColor());
            else {
                startingState.generateBranches(3, startingState.getColor());
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
            //System.out.println("time taken "+String.valueOf(System.currentTimeMillis()-startTime)+" brnaching size::"+startingState.getBranches().size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
