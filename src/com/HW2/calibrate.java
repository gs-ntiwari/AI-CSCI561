package com.HW2;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Set;

public class calibrate{

    public static void main(String[] args)
    {
        double error=0;
        long startTime= System.currentTimeMillis();
        Set<Coordinate> initialBlackPositions =Utility.getInitialPositions(CellType.Black);
        Set<Coordinate> initialWhitePositions =Utility.getInitialPositions(CellType.White);
        GameState initState = new GameState(initialWhitePositions, initialBlackPositions, CellType.Black, null);
        initState.generateBranches(1, CellType.Black);
        long totalTime = System.currentTimeMillis()-startTime;
        //error=Math.max(error,(totalTime-50)/50.0);
        System.out.println("Time taken for generating "+totalTime);
        startTime= System.currentTimeMillis();
        initState = new GameState(initialWhitePositions, initialBlackPositions, CellType.Black, null);
        initState.generateBranches(3, CellType.Black);
        totalTime = System.currentTimeMillis()-startTime;
        //error=Math.max(error,(totalTime-85)/85.0);
        System.out.println("Time taken for generating "+totalTime);
        startTime= System.currentTimeMillis();
        initState = new GameState(initialWhitePositions, initialBlackPositions, CellType.Black, null);
        initState.generateBranches(5, CellType.Black);
        totalTime = System.currentTimeMillis()-startTime;
        error=Math.max(error,(totalTime-1500)/1500.0);
        System.out.println("Time taken for generating "+totalTime);
        System.out.println(error);
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        //don't need monitor and synchronizer info，only get thread and stack info。
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        for (ThreadInfo threadInfo : threadInfos) {
            String tName = threadInfo.getThreadName();
            long tId = threadInfo.getThreadId();
            long time= threadMXBean.getThreadCpuTime(tId);
            System.out.println("....."+threadMXBean.getCurrentThreadCpuTime());
            System.out.println(tName + "，" + tId+" "+time/1000.0);
        }

    }

    /** Get CPU time in nanoseconds. */
    public long getCpuTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
                bean.getCurrentThreadCpuTime( ) : 0L;
    }
    /** Get user time in nanoseconds. */
    public long getUserTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
                bean.getCurrentThreadUserTime( ) : 0L;
    }


}
