package com.HW2;

import java.io.*;
import java.util.*;

public class Utility {

    public static Input readFile(String filename) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(filename));
        String isSinglePlayer = bf.readLine();
        boolean isSingle = false;
        if (isSinglePlayer.equals("SINGLE"))
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
                    board[count][j] = CellType.Empty;
                else if (array[j] == 'B')
                    blackPositions.add(new Coordinate(count, j));
                else
                    whitePositions.add(new Coordinate(count, j));
            }
            count++;
        }
        bf.close();
        GameState initState = new GameState(whitePositions, blackPositions, color.equals("Black") ? CellType.Black : CellType.White, null);
        return new Input(isSingle, time, initState);
    }

    public static void writeOutput(List<String> moveType, List<Coordinate> source, List<Coordinate> target) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"));
        for (int i = 0; i < moveType.size(); i++) {
            StringBuilder strb = new StringBuilder(moveType.get(i));
            strb.append(" ");
            strb.append(source.get(i));
            strb.append(" ");
            strb.append(target.get(i));
            if (i != moveType.size() - 1)
                ((BufferedWriter) writer).newLine();
        }
        writer.close();
    }


    public static boolean isValidMove(char moveType, int fromX, int fromY, int toX, int toY, Set<Coordinate> whitePositions, Set<Coordinate> blackPositions) {
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
        newPositions.remove(new Coordinate(fromX, fromY));
        newPositions.add(new Coordinate(toX, toY));
        return newPositions;
    }

    public static CellType flipColor(CellType color) {
        return color == CellType.Black ? CellType.White : CellType.Black;
    }
}
