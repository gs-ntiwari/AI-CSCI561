package com.HW2;
import java.io.*;
import java.util.stream.Stream;
public class MasterAgent {
    int moveCount=0;
    CellType ColorType=CellType.Black;
    CellType [][] board = new CellType[16][16];

    public void initPlay() throws IOException {
        for(int i=0;i<16;i++)
        {
            for(int j=0;j<16;j++)
            {
                board[i][j]=CellType.Empty;
            }
        }
        Input input=Utility.readFile("/Users/nishatiwari/CSCI561/src/com/HW2/input.txt");

        for(Coordinate position:input.getInitState().getBlackPositions())
        {
            this.board[position.x][position.y]=CellType.Black;
        }

        for(Coordinate position:input.getInitState().getWhitePositions())
        {
            this.board[position.x][position.y]=CellType.White;
        }
    }

    public void startPlay() throws IOException {
        while(true) {
            homework hm = new homework();
            hm.gamePlay();
            this.moveCount++;
            BufferedReader bf = new BufferedReader(new FileReader("/Users/nishatiwari/CSCI561/output.txt"));
            Stream<String> lines = bf.lines();
            System.out.println(this.ColorType);
            for (Object str : lines.toArray()) {
                String string = String.valueOf(str);
                System.out.println(string);
                String[] moves = string.split("\\s");
                String[] from = moves[1].split(",");
                String[] to = moves[2].split(",");
                System.out.println("Before "+board[Integer.parseInt(from[0])][Integer.parseInt(from[1])]+" "+board[Integer.parseInt(to[0])][Integer.parseInt(to[1])]);
                CellType temp = board[Integer.parseInt(from[0])][Integer.parseInt(from[1])];
                board[Integer.parseInt(from[0])][Integer.parseInt(from[1])] = CellType.Empty;
                board[Integer.parseInt(to[0])][Integer.parseInt(to[1])] = temp;
                System.out.println("After "+board[Integer.parseInt(from[0])][Integer.parseInt(from[1])]+" "+board[Integer.parseInt(to[0])][Integer.parseInt(to[1])]);
            }
            bf.close();
            if (isTerminalState()) {
                System.out.println("Game complete: winner is::" + this.ColorType);
                return;
            }
            else {
                System.out.println("current game configuration");
                for(int i=0;i<16;i++)
                {
                    for(int j=0;j<16;j++)
                    {
                        System.out.print(board[j][i]==CellType.Black?'B':(board[i][j]==CellType.White?'W':'.'));
                    }
                    System.out.println();
                }
                this.ColorType=Utility.flipColor(this.ColorType);
                writeToInputFile();
            }

        }

    }

    private void writeToInputFile() throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/nishatiwari/CSCI561/src/com/HW2/input.txt"), "utf-8"));
        writer.write("SINGLE");
        ((BufferedWriter) writer).newLine();
        writer.write(this.ColorType==CellType.Black?"BLACK":"WHITE");
        ((BufferedWriter) writer).newLine();
        writer.write("100");
        ((BufferedWriter) writer).newLine();
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

    private boolean isTerminalState() {
        for(int i=0;i<16;i++)
        {
            for(int j=0;j<16;j++)
            {
                if(board[j][i]==this.ColorType)
                    if(!Utility.getInitialPositions(Utility.flipColor(this.ColorType)).contains(new Coordinate(j, i)))
                        return false;
            }
        }
        return true;
    }

}
