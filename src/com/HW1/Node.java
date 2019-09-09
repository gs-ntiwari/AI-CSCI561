package com.HW1;

import java.util.List;

public class Node{
    Coordinate coordinate;
    int currentDepth=0;
    int currentCost=0;
    List<Coordinate> sequence;
    Node parent;
    Node(Coordinate coordinate, int currentCost, int currentDepth, Node parent)
    {
        this.coordinate=coordinate;
        this.currentCost=currentCost;
        this.currentDepth=currentDepth;
        this.parent=parent;
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