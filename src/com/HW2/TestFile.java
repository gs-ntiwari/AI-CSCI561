package com.HW2;

import javafx.scene.control.Cell;

import java.io.IOException;
import java.util.Set;

public class TestFile {

    public static  void main (String[] args) throws IOException {
        Input state=TestReadFile("input.txt");
        TestIsValidJump(state.getInitState().getWhitePositions(), state.getInitState().getBlackPositions(), state.getInitState().getColor());
        state=TestReadFile("input1.txt");
        TestIsValidJump1(state.getInitState().getWhitePositions(), state.getInitState().getBlackPositions(), state.getInitState().getColor());
    }

    private static void TestIsValidJump(Set<Coordinate> white, Set<Coordinate> black, CellType color) {
        assert Utility.isValidAdjacentMove(0,0, 1,0, white, black)==false;
        assert Utility.isValidAdjacentMove(0,4, 0,5, white, black)==true;
        assert Utility.isValidAdjacentMove(0,4, 1,5, white, black)==true;
        assert Utility.isValidAdjacentMove(3,1, 5,1, white, black)==false;
        assert Utility.isAValidJump(3,1, 5,1, white, black)==true;
        assert Utility.isAValidJump(2,1, 4,3, white, black)==true;
        assert Utility.isValidMove('J',0,0, 2,0, white, black,CellType.Black)==false;
        assert Utility.isValidMove('A',0,0, 1,1, white, black,CellType.Black)==false;
        assert Utility.isValidMove('A',2,3, 3,4, white, black,CellType.Black)==true;
        assert Utility.isValidMove('J',2,3, 3,4, white, black,CellType.Black)==false;
        assert Utility.isValidMove('J',15,13, 13,11, white, black,color)==true;
        assert Utility.isValidMove('J',11,14, 9,12, white, black,color)==false;
        assert Utility.isValidMove('J',12,14, 10,14, white, black,color)==true;
        assert Utility.isValidMove('J',12,14, 10,14, white, black,color)==true;
    }

    private static void TestIsValidJump1(Set<Coordinate> white, Set<Coordinate> black, CellType color) {
        assert Utility.isValidMove('A',0,0, 0,5, white, black,color)==false;
        assert Utility.isValidMove('A',0,4, 2,6, white, black,color)==false;
        assert Utility.isValidMove('J',4,1, 2,3, white, black,color)==true;
        assert Utility.isValidMove('A',3,3, 2,3, white, black,color)==true;
        assert Utility.isValidMove('J',1,1, 3,3, white, black,color)==false;
    }

    private static Input TestReadFile(String input) throws IOException {
        Input state=Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW2/"+input);
        assert state != null && state.time!=0;
        System.out.println(state.getInitState().getColor()+" "+state.isSinglePlayer+" "+state.time+" ");
        /*for(int i=0;i<16;i++)
        {
            for(int j=0;j<16;j++)
            {
                System.out.print(state.getInitState().board[i][j].ordinal()+" ");
            }
            System.out.println();
        }*/
        return state;
    }

}
