package com.HW1;

import java.util.List;

public class Test {
    public static void main(String[] args)
    {
        List<Coordinate> list=Utility.generateListCoordinatesFromString("0,0 0,1 1,2 2,1 3,0 4,1 4,2");
        int myresult=Utility.calculateCostBFS(list);
        list=Utility.generateListCoordinatesFromString("0,0 0,1 1,2 2,1 3,0 4,1 4,2");
        int expectedresult=Utility.calculateCostBFS(list);
        System.out.print("myresult:"+myresult+" expected:"+expectedresult);

    }
}
