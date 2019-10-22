package com.HW2;

import javafx.scene.control.Cell;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Utility {


    public static Set<Coordinate> getInitialPositions(CellType color)
    {
        Set<Coordinate> coordinates = new HashSet<>();
        if(CellType.White==color)
        {
            coordinates.add(new Coordinate(15, 15));
            coordinates.add(new Coordinate(14, 15));
            coordinates.add(new Coordinate(13, 15));
            coordinates.add(new Coordinate(12, 15));
            coordinates.add(new Coordinate(11, 15));
            coordinates.add(new Coordinate(15, 14));
            coordinates.add(new Coordinate(15, 13));
            coordinates.add(new Coordinate(15, 12));
            coordinates.add(new Coordinate(15, 11));
            coordinates.add(new Coordinate(14, 14));
            coordinates.add(new Coordinate(14, 13));
            coordinates.add(new Coordinate(14, 12));
            coordinates.add(new Coordinate(14, 11));
            coordinates.add(new Coordinate(13, 14));
            coordinates.add(new Coordinate(12, 14));
            coordinates.add(new Coordinate(11, 14));
            coordinates.add(new Coordinate(13, 13));
            coordinates.add(new Coordinate(12, 13));
            coordinates.add(new Coordinate(13, 12));
        }
        else
        {
            coordinates.add(new Coordinate(0, 0));
            coordinates.add(new Coordinate(1, 0));
            coordinates.add(new Coordinate(2, 0));
            coordinates.add(new Coordinate(3, 0));
            coordinates.add(new Coordinate(4, 0));
            coordinates.add(new Coordinate(0, 1));
            coordinates.add(new Coordinate(0, 2));
            coordinates.add(new Coordinate(0, 3));
            coordinates.add(new Coordinate(0, 4));
            coordinates.add(new Coordinate(1, 1));
            coordinates.add(new Coordinate(1, 2));
            coordinates.add(new Coordinate(1, 3));
            coordinates.add(new Coordinate(1, 4));
            coordinates.add(new Coordinate(2, 1));
            coordinates.add(new Coordinate(3, 1));
            coordinates.add(new Coordinate(4, 1));
            coordinates.add(new Coordinate(2, 2));
            coordinates.add(new Coordinate(2, 3));
            coordinates.add(new Coordinate(3, 2));
        }
        return coordinates;
    }

    public static Input readFile(String filename) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(filename));
        String isSinglePlayer = bf.readLine();
        boolean isSingle = false;
        if (isSinglePlayer.equalsIgnoreCase("SINGLE"))
            isSingle = true;
        String color = bf.readLine();
        double time = Double.parseDouble(bf.readLine());
        CellType[][] board = new CellType[16][16];
        Set<Coordinate> whitePositions = new HashSet<>();
        Set<Coordinate> blackPositions = new HashSet<>();
        int count = 0;
        while (count < 16) {
            char[] array = bf.readLine().toCharArray();
            for (int j = 0; j < array.length; j++) {
                if (array[j] == '.')
                    board[j][count] = CellType.Empty;
                else if (array[j] == 'B')
                    blackPositions.add(new Coordinate(j, count));
                else
                    whitePositions.add(new Coordinate(j, count));
            }
            count++;
        }
        bf.close();
        GameState initState = new GameState(whitePositions, blackPositions, color.equalsIgnoreCase("Black") ? CellType.Black : CellType.White, null);
        return new Input(isSingle, time, initState);
    }

    public static void writeOutput(GameState gameState) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"));
        List<Path> gamePath = gameState.getPath();
        for (int i = 0; i < gamePath.size(); i++) {
            StringBuilder strb = new StringBuilder(gamePath.get(i).moveType.getVal());
            strb.append(" ");
            strb.append(gamePath.get(i).from);
            strb.append(" ");
            strb.append(gamePath.get(i).to);
            writer.write(strb.toString());
            if (i != gamePath.size() - 1)
                ((BufferedWriter) writer).newLine();
        }
        writer.close();
    }


    public static boolean isValidMove(char moveType, int fromX, int fromY, int toX, int toY, Set<Coordinate> whitePositions, Set<Coordinate> blackPositions, CellType color) {
        boolean isValid;
        if (moveType == 'J') {
            isValid = isValidJumpMove(fromX, fromY, toX, toY, whitePositions, blackPositions);
        } else {
            isValid = isValidAdjacentMove(fromX, fromY, toX, toY, whitePositions, blackPositions);
        }
        return isValid;
    }

    private static boolean isValidJumpMove(int fromX, int fromY, int toX, int toY, Set<Coordinate> whitePositions, Set<Coordinate> blackPositions) {
        return isWithinBoundaries(toX, toY) && isEmpty(toX, toY, whitePositions, blackPositions) && isAValidJump(fromX, fromY, toX, toY, whitePositions, blackPositions);
    }

    public static boolean isAValidJump(int fromX, int fromY, int toX, int toY, Set<Coordinate> whitePositions, Set<Coordinate> blackPositions) {
        boolean result = false;
        if (Math.abs(toY - fromY) == 2 && Math.abs(toX - fromX) == 0) {
            if (!isEmpty(toX, fromY - (fromY - toY) / 2, whitePositions, blackPositions))
                result = true;
        } else if (Math.abs(toX - fromX) == 2 && Math.abs(toY - fromY) == 0) {
            if (!isEmpty(fromX - (fromX - toX) / 2, toY, whitePositions, blackPositions))
                result = true;
        } else if (Math.abs(toX - fromX) == 2 && Math.abs(toY - fromY) == 2) {
            if (!isEmpty(fromX - (fromX - toX) / 2, fromY - (fromY - toY) / 2, whitePositions, blackPositions))
                result = true;
        }
        return result;
    }

    public static boolean isValidAdjacentMove(int fromX, int fromY, int toX, int toY, Set<Coordinate> whitePositions, Set<Coordinate> blackPositions) {
        return isWithinBoundaries(toX, toY) && isEmpty(toX, toY, whitePositions, blackPositions) && isAdjacentCell(fromX, fromY, toX, toY);
    }

    public static boolean isEmpty(int toX, int toY, Set<Coordinate> whitePositions, Set<Coordinate> blackPositions) {
        if (!whitePositions.contains(new Coordinate(toX, toY)) && !blackPositions.contains(new Coordinate(toX, toY)))
            return true;
        return false;
    }


    public static boolean isAdjacentCell(int fromX, int fromY, int toX, int toY) {
        return (Math.abs(fromX - toX) == 1 && Math.abs(fromY - toY) == 0) || ((Math.abs(fromX - toX) == 0 && Math.abs(fromY - toY) == 1)) || ((Math.abs(fromX - toX) == 1 && Math.abs(fromY - toY) == 1));
    }

    public static boolean isWithinBoundaries(int toX, int toY) {
        return toX >= 0 && toX < 16 && toY >= 0 && toY < 16;
    }

    public static Set<Coordinate> createBoardCopy(Set<Coordinate> currentPositons, int fromX, int fromY, int toX, int toY) {
        Set<Coordinate> newPositions = new HashSet<>(currentPositons);
        if(!currentPositons.contains(new Coordinate(toX, toY)) && currentPositons.contains(new Coordinate(fromX, fromY))) {
            newPositions.remove(new Coordinate(fromX, fromY));
            newPositions.add(new Coordinate(toX, toY));
        }
        return newPositions;
    }

    public static CellType flipColor(CellType color) {
        return color == CellType.Black ? CellType.White : CellType.Black;
    }

    public static int updateMoveCount() throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream("/Users/nishatiwari/CSCI561/PlayData.ser");
        ObjectInputStream in = new ObjectInputStream(file);
        PlayData pd1 = (PlayData)in.readObject();
        file.close();
        in.close();
        int moveCount=pd1.getMoveCount();
        FileOutputStream file1 = new FileOutputStream("/Users/nishatiwari/CSCI561/PlayData.ser");
        ObjectOutputStream out = new ObjectOutputStream(file1);

        // Method for serialization of object
        pd1.setMoveCount(moveCount+1);
        out.writeObject(pd1);

        out.close();
        file1.close();

        in.close();
        return moveCount;
    }

    public static boolean isTerminalState(CellType color, Set<Coordinate> positions) {
        Set<Coordinate> intersection = new HashSet<>(positions);
        intersection.retainAll(getInitialPositions(Utility.flipColor(color)));
        return intersection.size()==19;

    }

    public static int filterOutBestNode(GameState startingState, List<Integer> indices) {
        int bextIndex=0;
        double maxDistanceCovered=Integer.MIN_VALUE;
        Map<Double, List<Integer>> mapOfDistances = new HashMap<>();
        for(int i:indices)
        {
            GameState state=startingState.getBranches().get(i);
            double d=calculateEuclideanDistance(state.getPath().get(0).from, startingState.getColor()==CellType.White?new Coordinate(0,0) :new Coordinate(15,15));
            if(d>maxDistanceCovered) {
                maxDistanceCovered = d;
                bextIndex=i;
            }
            if(mapOfDistances.containsKey(d))
            {
                List<Integer> bestIndices = mapOfDistances.get(d);
                bestIndices.add(i);
                mapOfDistances.put(d, bestIndices);
            }
            else
            {
                List<Integer> bestIndices = new ArrayList<>();
                bestIndices.add(i);
                mapOfDistances.put(d, bestIndices);
            }
        }
        List<Integer> leftIndices=mapOfDistances.get(maxDistanceCovered);
        //bextIndex=leftIndices.size()>1?leftIndices.get((new Random()).nextInt(leftIndices.size())):bextIndex;
        return bextIndex;
    }

    public static int filterOutBestNodeBasedOnEvalFunction(GameState startingState, List<Integer> indices) {
        if(indices==null)
            return 0;
        int bextIndex=0;
        int maxEval=Integer.MIN_VALUE;
        Map<Integer, List<Integer>> mapOfDistances = new HashMap<>();
        for(int i:indices)
        {
            GameState state=startingState.getBranches().get(i);
            if(state.getEvalValue()>maxEval) {
                maxEval = state.getEvalValue();
                bextIndex=i;
            }
            if(mapOfDistances.containsKey(state.getEvalValue()))
            {
                List<Integer> bestIndices = mapOfDistances.get(state.getEvalValue());
                bestIndices.add(i);
                mapOfDistances.put(state.getEvalValue(), bestIndices);
            }
            else
            {
                List<Integer> bestIndices = new ArrayList<>();
                bestIndices.add(i);
                mapOfDistances.put(state.getEvalValue(), bestIndices);
            }
        }
        List<Integer> leftIndices=mapOfDistances.get(maxEval);
        bextIndex=leftIndices.size()>1?leftIndices.get((new Random()).nextInt(leftIndices.size())):bextIndex;
        return bextIndex;
    }


    public static double calculateEuclideanDistance(Coordinate currPosition, Coordinate position) {
        return Math.sqrt((currPosition.x - position.x) ^ 2 + (currPosition.y - position.y) ^ 2);
    }

}
