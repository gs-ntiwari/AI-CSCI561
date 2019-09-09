package com.HW1;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utility {

    public static String makeSequence(List<Coordinate> sequence) {
        StringBuilder stringBuilder = new StringBuilder();
        for(Coordinate coordinate: sequence)
        {
            stringBuilder.append(coordinate);
            stringBuilder.append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    public static String makeSequence(HashMap<Coordinate, Coordinate> map, Coordinate target) {
        StringBuilder stringBuilder = new StringBuilder();
        Coordinate current = target;
        while(current!=null)
        {
            stringBuilder.insert(0, " "+current);
            current=map.get(current);
        }
        stringBuilder.deleteCharAt(0);
        return stringBuilder.toString();
    }

    public static void writeResultToFile(List<String> result, int N) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"));
        if(result.isEmpty() && N>0) {
            int count=0;
            while(count!=N) {
                writer.write("FAIL");
                count++;
            }
            writer.close();
            return;
        }
        for(int i=0;i<result.size();i++) {
            String str=result.get(i);
            if(str==null)
                writer.write("FAIL");
            else
                writer.append(str);
            if(i!=result.size()-1)
                ((BufferedWriter) writer).newLine();
        }
        int count=0;
        while(count!=N-result.size()) {
            writer.write("FAIL");
            count++;
        }

        writer.close();
    }

    public static Input readFile(String filename) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(filename));
        String algo=bf.readLine();
        String[] line=bf.readLine().split(" ");
        int W=(Integer.parseInt(line[0]));
        int H=(Integer.parseInt(line[1]));
        line=bf.readLine().split(" ");
        Coordinate landingSite= new Coordinate(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
        int maxElevation=Integer.parseInt(bf.readLine());
        int N=Integer.parseInt(bf.readLine());
        int count=0;
        List<Coordinate> targets = new ArrayList<Coordinate>();
        while(count!=N)
        {
            line=bf.readLine().split(" ");
            targets.add(new Coordinate(Integer.parseInt(line[0]), Integer.parseInt(line[1])));
            count++;
        }
        count=0;
        int[][] elevationMap= new int[W][H];
        while(count!=H)
        {
            line=bf.readLine().split(" ");
            int temp=0;
            while(temp!=W)
            {
                elevationMap[temp][count]=Integer.parseInt(line[temp]);
                temp++;
            }
            count++;
        }
        bf.close();
        return new Input(landingSite, maxElevation,targets,elevationMap,algo);
    }
}
