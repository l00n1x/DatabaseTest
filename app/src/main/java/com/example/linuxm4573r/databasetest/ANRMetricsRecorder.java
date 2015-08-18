package com.example.linuxm4573r.databasetest;

import android.util.Log;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;


public class ANRMetricsRecorder extends TimerTask {

    private int mainThreadPID;

    //We want a constructor so that the main thread
    //can pass along the PID for the main thread.
    public ANRMetricsRecorder(int mainPID)
    {
        super();
        mainThreadPID=mainPID;
    }



    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        //Each time this TimerTask runs we want to record cpu & io metrics.
        recordMetrics();
    }

    /**
     * Reads CPU activity using file in /proc path
     */
    private int getCurrentCPUActivity()
    {
        //TODO
        BufferedReader cpuProcFile = getCpuProcFile();
        int cpuActivity=-1;
        if(cpuProcFile!=null) {
            String line = readLineFromFile(cpuProcFile);
            String fields[] = line.split(" ");
            //Remember we start at fields[0] so this is
            //the 14th and 15 items in /proc/pid/task/pid/stat file.
            int utime = Integer.parseInt(fields[13]);
            int stime = Integer.parseInt(fields[14]);
            cpuActivity=utime+stime;
            try {
                cpuProcFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return cpuActivity;
    }

    /**
     * Reads in a line from a BufferedReader and return a string of that line.
     * @param in    BufferedReader to read line from.
     * @return  String of the line read from @param in.  Returns null if IOException occurred.
     */
    private String readLineFromFile(BufferedReader in)
    {
        String line = null;
        try {
            line = in.readLine();
        }
        catch(IOException e)
        {
            Log.d("ANRMetricsRecorder","ERROR WHILE READING FILE");
            e.printStackTrace();

        }
        return line;

    }

    /**
     * Reads IO activity using file in /proc path
     */
    private int getCurrentIOActivity()
    {
        //TODO
        BufferedReader ioProcFile = getIOProcFile();
        int ioActivity=-1;
        if(ioProcFile!=null)
        {
            int rchar = getNumberFromIOLine(readLineFromFile(ioProcFile));
            int wchar = getNumberFromIOLine(readLineFromFile(ioProcFile));
            int syscr = getNumberFromIOLine(readLineFromFile(ioProcFile));
            int syscw = getNumberFromIOLine(readLineFromFile(ioProcFile));
            int read_bytes = getNumberFromIOLine(readLineFromFile(ioProcFile));
            int write_bytes = getNumberFromIOLine(readLineFromFile(ioProcFile));
            try {
                ioProcFile.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return ioActivity;
    }

    private int getNumberFromIOLine(String line)
    {
        int spaceLocation = line.indexOf(' ');
        String number=line.substring(spaceLocation + 1, line.length() - 1);
        return Integer.parseInt(number);
    }


    /**
     * Method that will record all the metrics when called
     */
    private void recordMetrics()
    {
        //TODO
        int cpuActivity=getCurrentCPUActivity();
        int ioActivity=getCurrentIOActivity();
        BufferedWriter metricsFile=getRecorderFile();
        try {
            Log.d("ANRMetricsApp", "WRITING METRICS TO /sdcard/metrics_file");
            metricsFile.append(Integer.toString(cpuActivity));
            metricsFile.append(',');
            metricsFile.append(Integer.toString(ioActivity));
            metricsFile.newLine();
            metricsFile.close();
        }
        catch (IOException e)
        {
            Log.d("ANRMetricsApp","ERROR WHILE WRITING METRICS TO FILE");
        }
    }

    /**
     *
     */
    private BufferedReader getCpuProcFile()
    {
        StringBuilder path=new StringBuilder("/proc//task//stat");
        path.insert(12, String.valueOf(mainThreadPID));
        path.insert(6, String.valueOf(mainThreadPID));
        Log.d("ANRMetricsApp", new String("Opening " + path.toString() + " for reading...."));
        FileReader myFile=null;
        try {
            myFile = new FileReader(path.toString());
        }
        catch (FileNotFoundException e)
        {
            Log.d("ANRMetricsRecorder", "FILE NOT FOUND EXCEPTION WAS THROWN WHILE OPENING PROC CPU FILE");
        }
        return new BufferedReader(myFile);
    }

    /**
     Opens io Proc File for reading.
     * Returns null when file not found exception
     * is thrown.
     * Please check if null is returned.
     */
    private BufferedReader getIOProcFile()
    {
        StringBuilder path=new StringBuilder("/proc//io");
        path.insert(6, String.valueOf(mainThreadPID));
        BufferedReader myFile=null;
        try {
            myFile = new BufferedReader(new FileReader(path.toString()));
        }
        catch (FileNotFoundException e)
        {
            Log.d("ANRMetricsRecorder", "FILE NOT FOUND EXCEPTION WAS THROWN WHILE OPENING PROC IO FILE");
        }
        return myFile;
    }

    private BufferedWriter getRecorderFile()
    {

        String path = "/sdcard/metrics_file";
        FileWriter myFile=null;
        try {
            myFile = new FileWriter(path,true);
        }
        catch (IOException e)
        {
            Log.d("ANRMetricsRecorder", "COULD NOT OPEN /sdcard/metrics_file FOR WRITING!");
        }
        return new BufferedWriter(myFile);
    }


}
