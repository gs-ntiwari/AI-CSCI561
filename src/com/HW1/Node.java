package com.HW1;
public class Node{
    Coordinate coordinate;
    int currentDepth=0;
    int currentCost=0;
    int heuristicCost=0;
    Node parent;
    Node(Coordinate coordinate, int currentCost, int currentDepth, Node parent)
    {
        this.coordinate=coordinate;
        this.currentCost=currentCost;
        this.currentDepth=currentDepth;
        this.parent=parent;
    }
    Node(Coordinate coordinate, int currentCost, Integer heuristicCost, int currentDepth, Node parent)
    {
        this.coordinate=coordinate;
        this.currentCost=currentCost;
        this.currentDepth=currentDepth;
        this.parent=parent;
        this.heuristicCost=heuristicCost;
    }
    public boolean equals(Object obj) {
        if (!(obj instanceof Node))
            return false;
        if (obj == this)
            return true;
        Node node = (Node)obj;
        return this.coordinate.equals(node.coordinate);
    }

    public int hashCode(){
        return coordinate.hashCode();
    }

}