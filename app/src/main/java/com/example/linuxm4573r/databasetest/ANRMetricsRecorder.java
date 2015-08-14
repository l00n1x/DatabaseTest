package com.example.linuxm4573r.databasetest;

import android.util.Log;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.TimerTask;


public class ANRMetricsRecorder extends TimerTask {



    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {

    }

    /**
     * Reads CPU activity using file in /proc path
     */
    public int getCurrentCPUActivity()
    {
        //TODO
        FileReader cpuProcFile = getCpuProcFile();

        return 0;
    }

    /**
     * Reads IO activity using file in /proc path
     */
    public int getCurrentIOActivity()
    {
        //TODO
        FileReader ioProcFile = getIOProcFile();
        return 0;
    }

    /**
     * Returns the PID of the main thread
     */
    public int getMainPID()
    {
        //TODO
        int pid= android.os.Process.myPid();
        return pid;
    }

    /**
     * Method that will record all the metrics when called
     */
    public void recordMetrics()
    {
        //TODO
    }

    /**
     *
     */
    public FileReader getCpuProcFile()
    {
        StringBuilder path=new StringBuilder("/proc//stat");
        path.insert(6, String.valueOf(getMainPID()));
        FileReader myFile=null;
        try {
            myFile = new FileReader(path.toString());
        }
        catch (FileNotFoundException e)
        {
            Log.d("ANRMetricsRecorder", "FILE NOT FOUND EXCEPTION WAS THROWN WHILE OPENING PROC CPU FILE");
        }
        return myFile;
    }

    /**
     Opens io Proc File for reading.
     * Returns null when file not found exception
     * is thrown.
     * Please check if null is returned.
     */
    public FileReader getIOProcFile()
    {
        StringBuilder path=new StringBuilder("/proc//io");
        path.insert(6, String.valueOf(getMainPID()));
        FileReader myFile=null;
        try {
            myFile = new FileReader(path.toString());
        }
        catch (FileNotFoundException e)
        {
            Log.d("ANRMetricsRecorder", "FILE NOT FOUND EXCEPTION WAS THROWN WHILE OPENING PROC IO FILE");
        }
        return myFile;
    }


}
