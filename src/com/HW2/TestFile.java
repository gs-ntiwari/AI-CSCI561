package com.HW2;

import java.io.IOException;
import java.util.Set;

public class TestFile {

    public static  void main (String[] args) throws IOException {
        Input state=TestReadFile();
        TestIsValidJump(state.getInitState().getWhitePositions(), state.getInitState().getBlackPositions());
    }

    private static void TestIsValidJump(Set<Coordinate> white, Set<Coordinate> black) {
        assert Utility.isValidAdjacentMove(0,0, 1,0, white, black)==false;
        assert Utility.isValidAdjacentMove(0,4, 0,5, white, black)==true;
        assert Utility.isValidAdjacentMove(0,4, 1,5, white, black)==true;
        assert Utility.isValidAdjacentMove(3,1, 5,1, white, black)==false;
        assert Utility.isAValidJump(3,1, 5,1, white, black)==true;
        assert Utility.isAValidJump(2,1, 4,3, white, black)==true;
        assert Utility.isValidMove('J',0,0, 2,0, white, black)==false;
        assert Utility.isValidMove('A',0,0, 1,1, white, black)==false;
        assert Utility.isValidMove('A',2,3, 3,4, white, black)==true;
        assert Utility.isValidMove('J',2,3, 3,4, white, black)==false;
        assert Utility.isValidMove('J',15,13, 13,11, white, black)==true;
        assert Utility.isValidMove('J',11,14, 9,12, white, black)==false;
        assert Utility.isValidMove('J',12,14, 10,14, white, black)==true;
    }

    private static Input TestReadFile() throws IOException {
        Input state=Utility.readFile("/Users/nishatiwari/CSCI561/src/com.HW2/input.txt");
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
