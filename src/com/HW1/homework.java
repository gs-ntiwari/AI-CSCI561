package com.HW1;

import java.io.*;
import java.util.*;


public class homework {

    public static void main(String[] args) throws IOException
    {
        Input input =Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW1/input3_ucs.txt");
        List<String> result;

        if(input.algo.equals("BFS"))
        {
            result= runBFS(input.landingSite, input.maxElevation, input.targets, input.elevationMap);

        }
        else if(input.algo.equals(("UCS")))
        {
            result= runUCS(input.landingSite, input.maxElevation, input.targets, input.elevationMap);
        }
        else
        {
            result= runAstar(input.landingSite, input.maxElevation, input.targets, input.elevationMap);
        }

        /*System.out.println(algo+" "+W+" "+H+" "+landingSite.toString()+" "+maxElevation+" "+targets);
        for(int i=0;i<W;i++)
        {
            for(int j=0;j<H;j++)
            {
                System.out.print(elevationMap[i][j]);
            }
            System.out.println();
        }*/

        Utility.writeResultToFile(result, input.targets.size());

    }

    static class UCSComparator implements Comparator<Node>
    {
        public int compare(Node o1, Node o2) {
            if(o1.currentCost>o2.currentCost)
                return 1;
            else if(o1.currentCost<o2.currentCost)
                return -1;
            else
                return 0;
        }
    }


    private static List<String> runUCS(Coordinate landingSite, int maxElevation, List<Coordinate> targets, int[][] elevationMap) {

        String[] result = new String[targets.size()];
        Queue<Node> queue = new PriorityQueue<>(new UCSComparator());
        Node root = new Node(landingSite, 0, 0, null);
        HashMap<Coordinate, Coordinate> pathBacktracking = new HashMap<>();
        queue.add(root);
        pathBacktracking.put(root.coordinate, null);
        Set<Coordinate> close = new HashSet<>();
        HashMap<Coordinate, Node> queuedNodes = new HashMap<>();
        int noOfTargets=targets.size();
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            queuedNodes.put(currNode.coordinate, currNode);
            pathBacktracking.put(currNode.coordinate, currNode.parent==null?null:currNode.parent.coordinate);
            int index=targets.indexOf(currNode.coordinate);
            if (index!=-1)
            {
 //               List<Coordinate> temp = new ArrayList<>(currNode.sequence);
 //               temp.add(targets.get(index));
                result[index]= Utility.makeSequence(pathBacktracking, targets.get(index));
                noOfTargets--;
                if(noOfTargets==0)
                    break;
            }
            List<Node> children = expandNode(currNode, elevationMap, maxElevation);
            for(Node node:children)
            {
                if(!close.contains(node.coordinate) && !queue.contains(node)) {
                    queue.add(node);
                }
                else if(queuedNodes.containsKey(node.coordinate))
                {
                    Node queuedNode =queuedNodes.get(node.coordinate);
                    if(queuedNode.currentCost>node.currentCost)
                    {
                        queue.remove(queuedNode);
                        queue.add(node);
                    }
                }

            }
            close.add(currNode.coordinate);
        }

        return Arrays.asList(result);
    }

    private static List<String> runAstar(Coordinate landingSite, int maxElevation, List<Coordinate> targets, int[][] elevationMap) {
        List<String> result = new ArrayList<String>();
        return result;
    }

    private static List<String> runBFS(Coordinate landingSite, int maxElevation, List<Coordinate> targets, int[][] elevationMap) {
        String[] result = new String[targets.size()];
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(new Node(landingSite, 0, 0, null));
        Set<Coordinate> close = new HashSet<>();
        HashMap<Coordinate, Coordinate> pathBacktracking = new HashMap<>();
        int noOfTargets=targets.size();
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            pathBacktracking.put(currNode.coordinate, currNode.parent==null?null:currNode.parent.coordinate);
            close.add(currNode.coordinate);
            int index=targets.indexOf(currNode.coordinate);
            if (index!=-1)
            {
//                List<Coordinate> temp = new ArrayList<Coordinate>(currNode.sequence);
 //               temp.add(targets.get(index));
                result[index]= Utility.makeSequence(pathBacktracking, targets.get(index));
                noOfTargets--;
                if(noOfTargets==0)
                    break;
            }
            List<Node> children = expandNode(currNode, elevationMap, maxElevation);
            for(Node node:children)
            {
                if(!close.contains(node.coordinate)) {
                    queue.add(node);
                    close.add(node.coordinate);
                }

            }
        }

        return Arrays.asList(result);
    }

    private static List<Node> expandNode(Node currNode, int[][] elevationMap, int maxElevation) {
        int W= elevationMap.length;
        int H= elevationMap[0].length;
        List<Node> nodes = new ArrayList<Node>();
        int x=currNode.coordinate.x;
        int y=currNode.coordinate.y;
        //List<Coordinate> sequence = new ArrayList<Coordinate>(currNode.sequence);
        //sequence.add(currNode.coordinate);
        int diagonalCost=14;
        int straightCost=10;
        if(x-1>=0)
        {
            if(y-1>=0 && Math.abs(elevationMap[x][y]-elevationMap[x-1][y-1])<=maxElevation)
            {
                nodes.add(new Node(new Coordinate(x-1,y-1),currNode.currentCost+diagonalCost, currNode.currentDepth+1, currNode));
            }
            if(y+1<H && Math.abs(elevationMap[x][y]-elevationMap[x-1][y+1])<=maxElevation)
            {
                nodes.add(new Node(new Coordinate(x-1,y+1),currNode.currentCost+diagonalCost, currNode.currentDepth+1,  currNode));
            }
            if(Math.abs(elevationMap[x][y]-elevationMap[x-1][y])<=maxElevation)
            {
                nodes.add(new Node(new Coordinate(x - 1, y), currNode.currentCost+straightCost, currNode.currentDepth+1,  currNode));
            }
        }
        if(x+1<W)
        {
            if(y-1>=0 && Math.abs(elevationMap[x][y]-elevationMap[x+1][y-1])<=maxElevation)
            {
                nodes.add(new Node(new Coordinate(x+1,y-1),currNode.currentCost+diagonalCost, currNode.currentDepth+1, currNode));
            }
            if(y+1<H && Math.abs(elevationMap[x][y]-elevationMap[x+1][y+1])<=maxElevation)
            {
                nodes.add(new Node(new Coordinate(x+1,y+1),currNode.currentCost+diagonalCost, currNode.currentDepth+1, currNode));
            }
            if(Math.abs(elevationMap[x][y]-elevationMap[x+1][y])<=maxElevation) {
                nodes.add(new Node(new Coordinate(x + 1, y), currNode.currentCost+straightCost, currNode.currentDepth + 1, currNode));
            }
        }
        if(y-1>=0 && Math.abs(elevationMap[x][y]-elevationMap[x][y-1])<=maxElevation)
        {
            nodes.add(new Node(new Coordinate(x,y-1),currNode.currentCost+straightCost, currNode.currentDepth+1, currNode));
        }
        if(y+1<H && Math.abs(elevationMap[x][y]-elevationMap[x][y+1])<=maxElevation)
        {
            nodes.add(new Node(new Coordinate(x,y+1),currNode.currentCost+straightCost, currNode.currentDepth+1, currNode));
        }

        return nodes;
    }

}
