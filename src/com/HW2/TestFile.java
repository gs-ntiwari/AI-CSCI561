package com.HW2;

import java.io.*;
import java.util.Set;

public class TestFile {

    public static  void main (String[] args) throws IOException {
        long starttime=System.currentTimeMillis();
        /*PlayData pd = new PlayData();
        pd.setMoveCount(0);
        FileOutputStream file1 = new FileOutputStream("PlayData.ser");
        ObjectOutputStream out = new ObjectOutputStream(file1);
        // Method for serialization of object
        out.writeObject(pd);
        out.close();
        file1.close();
        Input state=TestReadFile("input.txt");
        TestIsValidJump(state.getInitState().getWhitePositions(), state.getInitState().getBlackPositions(), state.getInitState().getColor());
        state=TestReadFile("input1.txt");
        TestIsValidJump1(state.getInitState().getWhitePositions(), state.getInitState().getBlackPositions(), state.getInitState().getColor());*/
        MasterAgent ma = new MasterAgent();
        int totalCount=0;
        int depth=1;
        int min=Integer.MAX_VALUE;
        int max=Integer.MIN_VALUE;
        long maxTime=Integer.MIN_VALUE;
        long minTime=Integer.MAX_VALUE;
       for(int k=0;k<50;k++) {
           long time=System.currentTimeMillis();
           ma.initPlay();
           /*if(ma.ColorType==CellType.Black)
               ma.depth=1;
           else
               ma.depth=1;*/

           ma.startPlay(depth);
           long totalTime= System.currentTimeMillis()-time;
           maxTime=Math.max(totalTime, maxTime);
           minTime=Math.min(totalTime, minTime);
           System.out.println(ma.moveCount);
           totalCount+=ma.moveCount;
           max=Math.max(ma.moveCount, max);
           min=Math.min(ma.moveCount, min);
           ma.moveCount=0;
            Input input=Utility.readFile("/Users/nishatiwari/CSCI561/src/com/input_copy.txt");
           Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/nishatiwari/CSCI561/src/com/HW2/input.txt"), "utf-8"));
           writer.write("GAME");
           ((BufferedWriter) writer).newLine();
           writer.write("WHITE");
           ((BufferedWriter) writer).newLine();
           writer.write("100");
           ((BufferedWriter) writer).newLine();
           CellType[][] board = new CellType[16][16];
           for(int i=0;i<16;i++)
           {
               for(int j=0;j<16;j++)
               {
                   board[i][j]=CellType.Empty;
               }
           }

           for(Coordinate position:input.getInitState().getBlackPositions())
           {
               board[position.x][position.y]=CellType.Black;
           }

           for(Coordinate position:input.getInitState().getWhitePositions())
           {
               board[position.x][position.y]=CellType.White;
           }

           for(int i=0;i<16;i++) {
               StringBuilder strb = new StringBuilder();
               for (int j = 0; j<16; j++) {
                   strb.append(board[j][i]==CellType.Black?'B':(board[j][i]==CellType.White?'W':'.'));
               }
               writer.write(strb.toString());
               if (i != 15)
                   ((BufferedWriter) writer).newLine();
           }
           writer.close();
       }
       System.out.println(ma.map+" "+totalCount/20+" "+"max "+max+""+"min "+min+" "+"max Time "+maxTime+""+"min Time "+minTime+"");
       System.out.println((System.currentTimeMillis()-starttime)/20);
        //System.out.print(ma.map+" "+" "+ma.moveCount);
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
        //assert Utility.isValidMove('J',1,1, 2,3, white, black,color)==true;
        //assert Utility.isValidMove('A',3,3, 2,3, white, black,color)==true;
        assert Utility.isValidMove('J',1,1, 3,3, white, black,color)==true;
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
