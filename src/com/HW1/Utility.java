package com.HW1;

import java.io.*;
import java.util.*;

public class Utility {

    public static String makeSequence(HashMap<Coordinate, Coordinate> map, Coordinate target) {
        StringBuilder stringBuilder = new StringBuilder();
        Coordinate current = target;
        while (current != null) {
            stringBuilder.insert(0, " " + current);
            current = map.get(current);
        }
        stringBuilder.deleteCharAt(0);
        return stringBuilder.toString();
    }

    public static void writeResultToFile(String[] result) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"));
        /*if (result.isEmpty() && N > 0) {
            int count = 0;
            while (count != N) {
                writer.write("FAIL");
                count++;
            }
            writer.close();
            return;
        }*/
        for (int i = 0; i < result.length; i++) {
            String str = result[i];
            if (str == null)
                writer.write("FAIL");
            else
                writer.append(str);
            if (i != result.length - 1)
                ((BufferedWriter) writer).newLine();
        }
        /*int count = 0;
        while (count != N - result.size()) {
            writer.write("FAIL");
            count++;
        }*/

        writer.close();
    }

    public static Input readFile(String filename) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(filename));
        String algo = bf.readLine();
        String[] line = bf.readLine().split("\\s+");
        int W = (Integer.parseInt(line[0]));
        int H = (Integer.parseInt(line[1]));
        line = bf.readLine().split("\\s+");
        Coordinate landingSite = new Coordinate(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
        int maxElevation = Integer.parseInt(bf.readLine());
        int N = Integer.parseInt(bf.readLine());
        int count = 0;
        List<Coordinate> targets = new ArrayList<Coordinate>();
        while (count != N) {
            line = bf.readLine().split("\\s+");
            targets.add(new Coordinate(Integer.parseInt(line[0]), Integer.parseInt(line[1])));
            count++;
        }
        count = 0;
        int[][] elevationMap = new int[W][H];
        while (count != H) {
            line = bf.readLine().split("\\s+");
            int temp = 0;
            while (temp != W) {
                try {
                    elevationMap[temp][count] = Integer.parseInt(line[temp]);
                    temp++;
                } catch (Exception e) {
                    System.out.print(line[temp]);
                }
            }
            count++;
        }
        bf.close();
        return new Input(landingSite, maxElevation, targets, elevationMap, algo);
    }

    public static Input readFileForTest(String filename) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(filename));
        String algo = bf.readLine();
        String[] line = bf.readLine().split("\\s+");
        int W = (Integer.parseInt(line[0]));
        int H = (Integer.parseInt(line[1]));
        line = bf.readLine().split("\\s+");
        Coordinate landingSite = new Coordinate(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
        int maxElevation = Integer.parseInt(bf.readLine());
        int N = Integer.parseInt(bf.readLine());
        int count = 0;
        List<Coordinate> targets = new ArrayList<Coordinate>();
        while (count != N) {
            line = bf.readLine().split("\\s+");
            targets.add(new Coordinate(Integer.parseInt(line[0]), Integer.parseInt(line[1])));
            count++;
        }
        count = 0;
        int[][] elevationMap = new int[W][H];
        while (count != H) {
            line = bf.readLine().split("\\s+");
            int temp = 0;
            while (temp != W) {
                elevationMap[temp][count] = Integer.parseInt(line[temp]);
                temp++;
            }
            count++;
        }
        count = 0;
        List<String> output = new ArrayList<>();
        while (count != N) {
            output.add(bf.readLine());
            count++;
        }
        bf.close();
        return new Input(landingSite, maxElevation, targets, elevationMap, algo, output);
    }

    public static List<Coordinate> generateListCoordinatesFromString(String str) {
        List<Coordinate> coordinatesList = new ArrayList<>();
        String[] coordinates = str.split(" ");
        for (String coordinate : coordinates) {
            String[] xy = coordinate.split(",");
            coordinatesList.add(new Coordinate(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
        }
        return coordinatesList;
    }

    public static int calculateCost(List<Coordinate> sequence, int diagonalCost, int straightCost) {
        Coordinate prev = sequence.get(0);
        int totalCost = 0;
        for (int i = 1; i < sequence.size(); i++) {
            Coordinate currentCoordinate = sequence.get(i);
            if (Math.abs(currentCoordinate.x - prev.x) == 1 && Math.abs(currentCoordinate.y - prev.y) == 1) {
                totalCost += diagonalCost;
            } else {
                totalCost += straightCost;
            }
            prev = currentCoordinate;
        }
        return totalCost;
    }


    public static int[][] precomputeHeuristic(int[][] elevationMap, List<Coordinate> targets)

    {
        //long startTime=System.currentTimeMillis();
        int[][] result = new int[elevationMap.length][elevationMap[0].length];
        for (int i = 0; i < elevationMap.length; i++) {
            for (int j = 0; j < elevationMap[0].length; j++) {
                int minCost = Integer.MAX_VALUE;
                for (Coordinate target : targets) {
                    //int first = Math.abs(target.x - i);
                    //int second = Math.abs(target.y - j);
                    int temp = (int) (10 * Math.sqrt((target.x - i) ^ 2 + (target.y - j) ^ 2)) + Math.abs(elevationMap[target.x][target.y] - elevationMap[i][j]);
                    //int temp=first>second?(first*10)+(14*(second-1)):(second*10)+(14*(first-1));
                    //temp+=Math.abs(elevationMap[target.x][target.y] - elevationMap[i][j]);
                    //int temp= 10*Math.max(first, second);//+Math.abs(elevationMap[target.x][target.y] - elevationMap[i][j]);
                    //int temp = (first > second ? ((14 * first) + (10 * second-first)) : ((10 * first) + (14 * second)))
                    //+ Math.abs(elevationMap[target.x][target.y] - elevationMap[i][j]);
                    minCost = Math.min(minCost, temp);
                }
                result[i][j] = minCost;
            }
        }
        //System.out.println("Time for calculating heuristic::"+String.valueOf(System.currentTimeMillis()-startTime));
        return result;
    }


    public static boolean isConstraintSatisfied(List<Coordinate> coordinates, int[][] map, int maxEval) {
        boolean result = true;
        Coordinate prev = coordinates.get(0);
        for (int j = 1; j < coordinates.size(); j++) {
            Coordinate curr = coordinates.get(j);
            if (!isValidMove(map, maxEval, prev.x, prev.y, curr.x, curr.y)) {
                result = false;
                break;
            }
            prev = curr;

        }
        return result;
    }

    public static boolean isValidMove(int[][] elevationMap, int maxElevation, int x, int y, int x1, int y1) {
        int W = elevationMap.length;
        int H = elevationMap[0].length;
        return isValidCoordinate(x1, W) && isValidCoordinate(y1, H) && Math.abs(elevationMap[x][y] - elevationMap[x1][y1]) <= maxElevation;
    }

    private static boolean isValidCoordinate(int x, int w) {
        return x >= 0 && x < w;
    }


    public static int calculateCost(List<Coordinate> sequence, int diagonalCost, int straightCost, int[][] elevationMap) {
        Coordinate prev = sequence.get(0);
        int totalCost = 0;
        for (int i = 1; i < sequence.size(); i++) {
            Coordinate currentCoordinate = sequence.get(i);
            if (Math.abs(currentCoordinate.x - prev.x) == 1 && Math.abs(currentCoordinate.y - prev.y) == 1) {
                totalCost += diagonalCost;
            } else {
                totalCost += straightCost;
            }
            totalCost += Math.abs(elevationMap[prev.x][prev.y] - elevationMap[currentCoordinate.x][currentCoordinate.y]);
            prev = currentCoordinate;
        }
        return totalCost;
    }

    public static void printResults(String algo, int[][] elevationMap, String[] result) {
        if (algo.equals("BFS")) {
            for (int i = 0; i < result.length; i++) {
                if (result[i] != null)
                    System.out.println("Cost::" + Utility.calculateCost(Utility.generateListCoordinatesFromString(result[i]), 1, 1));
                else
                    System.out.println("Cost::" + "FAIL");
            }


        } else if (algo.equals(("UCS"))) {
            for (int i = 0; i < result.length; i++) {
                if (result[i] != null)
                    System.out.println("Cost::" + Utility.calculateCost(Utility.generateListCoordinatesFromString(result[i]), 14, 10));
                else
                    System.out.println("Cost::" + "FAIL");
            }
        } else {
            for (int i = 0; i < result.length; i++) {
                if (result[i] != null)
                    System.out.println("Cost::" + Utility.calculateCost(Utility.generateListCoordinatesFromString(result[i]), 14, 10, elevationMap));
                else
                    System.out.println("Cost::" + "FAIL");
            }
        }
    }
}
