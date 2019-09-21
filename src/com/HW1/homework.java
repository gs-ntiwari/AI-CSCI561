package com.HW1;

import java.io.*;
import java.util.*;


public class homework {

    public static void TestCode(Input input) throws IOException {
        List<String> result;
        if (input.algo.equals("BFS")) {
            result = runBFS(input.landingSite, input.maxElevation, input.targets, input.elevationMap);

        } else if (input.algo.equals(("UCS"))) {
            result = runUCS(input.landingSite, input.maxElevation, input.targets, input.elevationMap);
        } else {
            result = runAstar(input.landingSite, input.maxElevation, input.targets, input.elevationMap);
        }
        Utility.writeResultToFile(result, input.targets.size());
    }

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        Input input = Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW1/input13.txt");
        //System.out.println("Total time taken for reading a file in ms::"+ String.valueOf(System.currentTimeMillis()-startTime));
        List<String> result;
        if (input.algo.equals("BFS")) {
            result = runBFS(input.landingSite, input.maxElevation, input.targets, input.elevationMap);
        } else if (input.algo.equals(("UCS"))) {
            result = runUCS(input.landingSite, input.maxElevation, input.targets, input.elevationMap);
        } else {
            result = runAstar(input.landingSite, input.maxElevation, input.targets, input.elevationMap);
        }
        Utility.printResults(input.algo, input.elevationMap, result);
        Utility.writeResultToFile(result, input.targets.size());
        //System.out.println("Total time taken for writing to a file in ms::"+ String.valueOf(System.currentTimeMillis()-startTime2));
        System.out.println("Total time taken in ms::" + String.valueOf(System.currentTimeMillis() - startTime));
    }

    static class UCSComparator implements Comparator<Node> {
        public int compare(Node o1, Node o2) {
            if ((o1.currentCost + o1.heuristicCost) >= (o2.currentCost + o2.heuristicCost))
                return 1;
            else if ((o1.currentCost + o1.heuristicCost) < (o2.currentCost + o2.heuristicCost))
                return -1;
            return 0;
        }
    }


    private static List<String> runUCS(Coordinate landingSite, int maxElevation, List<Coordinate> targets, int[][] elevationMap) {
        String[] result = new String[targets.size()];
        //int[][] heuristics = Utility.precomputeHeuristic(elevationMap, targets);
        Queue<Node> queue = new PriorityQueue<>(new UCSComparator());
        Node root = new Node(landingSite, 0, 0, 0, null);
        HashMap<Coordinate, Coordinate> pathBacktracking = new HashMap<>();
        queue.add(root);
        pathBacktracking.put(root.coordinate, null);
        Set<Coordinate> close = new HashSet<>();
        HashMap<Coordinate, Node> queuedNodes = new HashMap<>();
        queuedNodes.put(root.coordinate, root);
        int noOfTargets = targets.size();
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            int index = targets.indexOf(currNode.coordinate);
            if (index != -1) {
                result[index] = Utility.makeSequence(pathBacktracking, targets.get(index));
                noOfTargets--;
                if (noOfTargets == 0)
                    break;
            }
            List<Node> children = expandNode(currNode, elevationMap, maxElevation);
            for (Node node : children) {
                if (!close.contains(node.coordinate) && !queue.contains(node)) {
                    queue.add(node);
                    queuedNodes.put(node.coordinate, node);
                    pathBacktracking.put(node.coordinate, node.parent == null ? null : node.parent.coordinate);
                } else if (queuedNodes.containsKey(node.coordinate)) {
                    Node queuedNode = queuedNodes.get(node.coordinate);
                    if (queuedNode.currentCost > node.currentCost) {
                        queue.remove(queuedNode);
                        queue.add(node);
                        queuedNodes.put(node.coordinate, node);
                        pathBacktracking.put(node.coordinate, node.parent == null ? null : node.parent.coordinate);
                    }
                }

            }
            close.add(currNode.coordinate);
        }

        return Arrays.asList(result);
    }

    private static List<String> runAstar(Coordinate landingSite, int maxElevation, List<Coordinate> targets, int[][] elevationMap) {
        String[] result = new String[targets.size()];
        int noOfTargets = targets.size();
        int[][] heuristics = Utility.precomputeHeuristic(elevationMap, targets);
        Queue<Node> queue = new PriorityQueue<>(new UCSComparator());
        Node root = new Node(landingSite, 0, 0, null);
        HashMap<Coordinate, Coordinate> pathBacktracking = new HashMap<>();
        queue.add(root);
        pathBacktracking.put(root.coordinate, null);
        Set<Coordinate> close = new HashSet<>();
        HashMap<Coordinate, Node> queuedNodes = new HashMap<>();
        queuedNodes.put(root.coordinate, root);
        //List<Coordinate> updatedTargets= new ArrayList<>(targets);
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            int index = targets.indexOf(currNode.coordinate);
            if (index != -1) {
                result[index] = Utility.makeSequence(pathBacktracking, targets.get(index));
                noOfTargets--;
                if (noOfTargets == 0)
                    break;
                //updatedTargets.remove(currNode.coordinate);
                //heuristics=Utility.precomputeHeuristic(elevationMap, updatedTargets);
                //updateQueuedNodes(queuedNodes, heuristics);
            }
            List<Node> children = expandNode(currNode, elevationMap, maxElevation, heuristics, true);
            for (Node node : children) {
                if (!close.contains(node.coordinate) && !queue.contains(node)) {
                    queue.add(node);
                    queuedNodes.put(node.coordinate, node);
                    pathBacktracking.put(node.coordinate, node.parent == null ? null : node.parent.coordinate);
                } else if (queuedNodes.containsKey(node.coordinate)) {
                    Node queuedNode = queuedNodes.get(node.coordinate);
                    if ((queuedNode.currentCost + queuedNode.heuristicCost) > (node.currentCost + node.heuristicCost)) {
                        queue.remove(queuedNode);
                        queue.add(node);
                        queuedNodes.put(node.coordinate, node);
                        pathBacktracking.put(node.coordinate, node.parent == null ? null : node.parent.coordinate);
                    }
                }
            }
            close.add(currNode.coordinate);
        }
        return Arrays.asList(result);
}

    private static void updateQueuedNodes(HashMap<Coordinate, Node> queuedNodes, int[][] heuristics) {
        for(Map.Entry entry:queuedNodes.entrySet())
        {
            Node currentNode=(Node)entry.getValue();
            currentNode.heuristicCost=heuristics[currentNode.coordinate.x][currentNode.coordinate.y];
            queuedNodes.put(currentNode.coordinate, currentNode);
        }
    }

    private static List<String> runBFS(Coordinate landingSite, int maxElevation, List<Coordinate> targets, int[][] elevationMap) {
        String[] result = new String[targets.size()];
        HashMap<Coordinate, Coordinate> pathBacktracking = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        Node root = new Node(landingSite, 0, 0, null);
        queue.add(root);
        Set<Coordinate> close = new HashSet<>();
        int noOfTargets = targets.size();
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            pathBacktracking.put(currNode.coordinate, currNode.parent == null ? null : currNode.parent.coordinate);
            close.add(currNode.coordinate);
            int index = targets.indexOf(currNode.coordinate);
            if (index != -1) {
                result[index] = Utility.makeSequence(pathBacktracking, targets.get(index));
                noOfTargets--;
                if (noOfTargets == 0)
                    break;
            }
            List<Node> children = expandNode(currNode, elevationMap, maxElevation);
            for (Node node : children) {
                if (!close.contains(node.coordinate)) {
                    queue.add(node);
                    close.add(node.coordinate);
                }

            }
        }
        return Arrays.asList(result);
    }

    private static List<Node> expandNode(Node currNode, int[][] elevationMap, int maxElevation) {
        //System.out.println("expand node start");
        int W = elevationMap.length;
        int H = elevationMap[0].length;
        List<Node> nodes = new ArrayList<Node>();
        int x = currNode.coordinate.x;
        int y = currNode.coordinate.y;
        int diagonalCost = 14;
        int straightCost = 10;
        if (x - 1 >= 0) {
            if (y - 1 >= 0 && Utility.isValidMove(elevationMap, maxElevation, x, y, x - 1, y - 1)) {
                addNodeToList(currNode, nodes, diagonalCost, x - 1, y - 1);
            }
            if (y + 1 < H && Utility.isValidMove(elevationMap, maxElevation, x, y, x - 1, y + 1)) {
                addNodeToList(currNode, nodes, diagonalCost, x - 1, y + 1);
            }
            if (Utility.isValidMove(elevationMap, maxElevation, x, y, x - 1, y)) {
                addNodeToList(currNode, nodes, straightCost, x - 1, y);
            }
        }
        if (x + 1 < W) {
            if (y - 1 >= 0 && Utility.isValidMove(elevationMap, maxElevation, x, y, x + 1, y - 1)) {
                addNodeToList(currNode, nodes, diagonalCost, x + 1, y - 1);
            }
            if (y + 1 < H && Utility.isValidMove(elevationMap, maxElevation, x, y, x + 1, y + 1)) {
                addNodeToList(currNode, nodes, diagonalCost, x + 1, y + 1);
            }
            if (Utility.isValidMove(elevationMap, maxElevation, x, y, x + 1, y)) {
                addNodeToList(currNode, nodes, straightCost, x + 1, y);
            }
        }
        if (y - 1 >= 0 && Utility.isValidMove(elevationMap, maxElevation, x, y, x, y - 1)) {
            addNodeToList(currNode,nodes, straightCost, x, y - 1);
        }
        if (y + 1 < H && Utility.isValidMove(elevationMap, maxElevation, x, y, x, y + 1)) {
            addNodeToList(currNode,nodes, straightCost, x, y + 1);
        }
        //System.out.println("expand node end");
        return nodes;
    }

    private static List<Node> expandNode(Node currNode, int[][] elevationMap, int maxElevation,int[][] heuristics, boolean isAStar) {
        //System.out.println("expand node start");
        int W = elevationMap.length;
        int H = elevationMap[0].length;
        List<Node> nodes = new ArrayList<Node>();
        int x = currNode.coordinate.x;
        int y = currNode.coordinate.y;
        int diagonalCost = 14;
        int straightCost = 10;
        if (x - 1 >= 0) {
            if (y - 1 >= 0 && Utility.isValidMove(elevationMap, maxElevation, x, y, x - 1, y - 1)) {
                addNodeToList(currNode,  nodes, diagonalCost, x - 1, y - 1, isAStar ? elevationMap : null, x, y, heuristics);
            }
            if (y + 1 < H && Utility.isValidMove(elevationMap, maxElevation, x, y, x - 1, y + 1)) {
                addNodeToList(currNode, nodes, diagonalCost, x - 1, y + 1, isAStar ? elevationMap : null, x, y,heuristics);
            }
            if (Utility.isValidMove(elevationMap, maxElevation, x, y, x - 1, y)) {
                addNodeToList(currNode, nodes, straightCost, x - 1, y, isAStar ? elevationMap : null, x, y,heuristics);
            }
        }
        if (x + 1 < W) {
            if (y - 1 >= 0 && Utility.isValidMove(elevationMap, maxElevation, x, y, x + 1, y - 1)) {
                addNodeToList(currNode, nodes, diagonalCost, x + 1, y - 1, isAStar ? elevationMap : null, x, y,heuristics);
            }
            if (y + 1 < H && Utility.isValidMove(elevationMap, maxElevation, x, y, x + 1, y + 1)) {
                addNodeToList(currNode, nodes, diagonalCost, x + 1, y + 1, isAStar ? elevationMap : null, x, y,heuristics);
            }
            if (Utility.isValidMove(elevationMap, maxElevation, x, y, x + 1, y)) {
                addNodeToList(currNode, nodes, straightCost, x + 1, y, isAStar ? elevationMap : null, x, y,heuristics);
            }
        }
        if (y - 1 >= 0 && Utility.isValidMove(elevationMap, maxElevation, x, y, x, y - 1)) {
            addNodeToList(currNode,  nodes, straightCost, x, y - 1, isAStar ? elevationMap : null, x, y,heuristics);
        }
        if (y + 1 < H && Utility.isValidMove(elevationMap, maxElevation, x, y, x, y + 1)) {
            addNodeToList(currNode,  nodes, straightCost, x, y + 1, isAStar ? elevationMap : null, x, y,heuristics);
        }
        //System.out.println("expand node end");
        return nodes;
    }


    private static void addNodeToList(Node currNode, List<Node> nodes, int diagonalCost, int i, int i2) {
        Coordinate child = new Coordinate(i, i2);
        nodes.add(new Node(child, currNode.currentCost + diagonalCost, currNode.currentDepth + 1, currNode));
    }

    private static void addNodeToList(Node currNode, List<Node> nodes, int diagonalCost, int i, int i2, int[][] elevationMap, int x, int y, int[][] heuristic) {
        Coordinate child = new Coordinate(i, i2);
        int g=currNode.currentCost + diagonalCost+ (elevationMap != null ? Math.abs(elevationMap[x][y] - elevationMap[i][i2]) : 0);
        int h=heuristic[child.x][child.y];
        nodes.add(new Node(child, g, h, currNode.currentDepth + 1, currNode));
    }
}