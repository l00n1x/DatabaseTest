package com.googlecode.droidwall;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;


import android.content.Context;
import android.content.SharedPreferences;
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
    public static final String PREFS_NAME = "MyPrefsFile";
    public String Previous_IO="Previous_IO_Activity";
    public int IO_Treshold = 1000;
    public String Previous_CPU="Previous_CPU_Activity";
    public int CPU_Treshold = 100000;
    Boolean is_stack_generated=false;
    public Context con;
    StringBuilder path_CPU;
    StringBuilder path_IO;
    String Generate_Stack;
    int sampling_delay_ms=500;

    int Previous_CPUActivity=-1;
    int Previous_ioActivity=-1;

    //We want a constructor so that the main thread
    //can pass along the PID for the main thread.
    public ANRMetricsRecorder(int mainPID,Context c)
    {
        super();
        mainThreadPID=mainPID;
        this.con=c;
        path_CPU = new StringBuilder("/proc//task//stat");
        path_CPU.insert(12, String.valueOf(mainThreadPID));
        path_CPU.insert(6, String.valueOf(mainThreadPID));
        path_IO=new StringBuilder("/proc//task//io");
        path_IO.insert(12, String.valueOf(mainThreadPID));
        path_IO.insert(6, String.valueOf(mainThreadPID));
        Generate_Stack= "su -c kill -3 "+mainThreadPID;



    }



    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        //Each time this TimerTask runs we want to record cpu & io metrics.
        while(true) {
            recordMetrics();
            //Log.v("Metrics", "going to sleep");
            SystemClock.sleep(sampling_delay_ms);
            }
    }

    private void recordMetrics()
    {
        //Log.v("Metrics", "woke up! Initiating procedure for sampling");
        int cpuActivity_diff=getCurrentCPUActivity();
        int ioActivity_diff=getCurrentIOActivity();
        try {
          //  Log.d("ANRMetricsApp", "WRITING METRICS TO /sdcard/metrics_file");
            if(is_stack_generated) {
            BufferedWriter metricsFile=getRecorderFile();
            metricsFile.append(Integer.toString(cpuActivity_diff));
            metricsFile.append(',');
            metricsFile.append(Integer.toString(ioActivity_diff));
                metricsFile.append(',');
                metricsFile.append("*");
            metricsFile.newLine();
            metricsFile.close();
                is_stack_generated=false;
            }

        }
        catch (IOException e)
        {
            Log.d("ANRMetricsApp","ERROR WHILE WRITING METRICS TO FILE");
        }
    }



    /**
     * Reads CPU activity using file in /proc path
     */
    private int getCurrentCPUActivity()
    {
        BufferedReader cpuProcFile = getCpuProcFile();
        int cpuActivity=-1;
        int CPU_difference = -1;

        if(cpuProcFile!=null) {
            String line = readLineFromFile(cpuProcFile);
            String fields[] = line.split(" ");
            //Remember we start at fields[0] so this is
            //the 14th and 15 items in /proc/pid/task/pid/stat file.
            int utime = Integer.parseInt(fields[13]);
            int stime = Integer.parseInt(fields[14]);
            cpuActivity=utime+stime;

            //SharedPreferences settings = con.getSharedPreferences(PREFS_NAME, 0);
            //int Previous_CPUActivity = settings.getInt(Previous_CPU,-1);

            // if this is the first run, then don't do anything. Just save the ioactivity for next run;
            if (Previous_CPUActivity!=-1){
                CPU_difference = cpuActivity-Previous_CPUActivity;

                if(CPU_difference>=0 && CPU_difference>CPU_Treshold) {
                    //Generate the stack trace with su kill -3 pid
                    try {
                        Runtime.getRuntime().exec(Generate_Stack);
                        is_stack_generated=true;
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }else if(CPU_difference>=0 && CPU_difference<=CPU_Treshold){
                    //Log.v("Metric","Threshold not exceeded");
                }else{
                    //Note that when io_difference is negative, the second condition is not verified as well so it is useless to handle the two cases separately.
                    Log.e("Metric","there is something wrong");
                }
            }else {
                //Log.v("Metric","This is the first iteration");
                //SharedPreferences.Editor editor = settings.edit();
                //editor.putInt(Previous_CPU, cpuActivity);
                //editor.commit();
                Previous_CPUActivity=cpuActivity;

                try {
                    cpuProcFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return 0;
            }

            //SharedPreferences.Editor editor = settings.edit();
            //editor.putInt(Previous_CPU, cpuActivity);
            //editor.commit();
            Previous_CPUActivity=cpuActivity;

            try {
                cpuProcFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return CPU_difference;
    }

    private BufferedReader getCpuProcFile()
    {
        //Log.d("ANRMetricsApp", new String("Opening " + path.toString() + " for reading...."));
        FileReader myFile=null;
        try {
            myFile = new FileReader(path_CPU.toString());
        }
        catch (FileNotFoundException e)
        {
            Log.d("ANRMetricsRecorder", "FILE NOT FOUND EXCEPTION WAS THROWN WHILE OPENING PROC CPU FILE");
        }
        return new BufferedReader(myFile);
    }




    private int getCurrentIOActivity()
    {
        BufferedReader ioProcFile = getIOProcFile();
        int ioActivity=-1;
        int io_difference=-1;
        if(ioProcFile!=null)
        {
            int rchar = getNumberFromIOLine(readLineFromFile(ioProcFile));
            int wchar = getNumberFromIOLine(readLineFromFile(ioProcFile));
            //int syscr = getNumberFromIOLine(readLineFromFile(ioProcFile));
            //int syscw = getNumberFromIOLine(readLineFromFile(ioProcFile));
            //int read_bytes = getNumberFromIOLine(readLineFromFile(ioProcFile));
            //int write_bytes = getNumberFromIOLine(readLineFromFile(ioProcFile));
            //int cancelled_bytes = getNumberFromIOLine(readLineFromFile(ioProcFile));

            ioActivity=rchar+wchar;

            //SharedPreferences settings = con.getSharedPreferences(PREFS_NAME, 0);
            //int Previous_ioActivity = settings.getInt(Previous_IO,-1);

            // if this is the first run, then don't do anything. Just save the ioactivity for next run;
            if (Previous_ioActivity!=-1){
                io_difference = ioActivity-Previous_ioActivity;

                if(io_difference>=0 && io_difference>IO_Treshold) {
                    //Generate the stack trace with su kill -3 pid
                    try {
                        Runtime.getRuntime().exec(Generate_Stack);
                        is_stack_generated=true;
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }else if(io_difference>=0 && io_difference<=IO_Treshold){
                    //Log.v("Metric","Threshold not exceeded");
                }else{
                    //Note that when io_difference is negative, the second condition is not verified as well so it is useless to handle the two cases separately.
                    Log.e("Metric","there is something wrong");
                }
            }else {
                //Log.v("Metric","This is the first iteration");
                //SharedPreferences.Editor editor = settings.edit();
                //editor.putInt(Previous_IO, ioActivity);
                //editor.commit();
                Previous_ioActivity=ioActivity;

                try {
                    ioProcFile.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return 0;
            }

            //SharedPreferences.Editor editor = settings.edit();
            //editor.putInt(Previous_IO, ioActivity);
            //editor.commit();
                Previous_ioActivity=ioActivity;
            try {
                ioProcFile.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return io_difference;
    }

    /**
     Opens io Proc File for reading.
     * Returns null when file not found exception
     * is thrown.
     * Please check if null is returned.
     */
    private BufferedReader getIOProcFile()
    {
        BufferedReader myFile=null;
        try {
            myFile = new BufferedReader(new FileReader(path_IO.toString()));
        }
        catch (FileNotFoundException e)
        {
            Log.d("ANRMetricsRecorder", "FILE NOT FOUND EXCEPTION WAS THROWN WHILE OPENING PROC IO FILE");
        }
        return myFile;
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
            Log.d("ANRMetricsRecorder", "ERROR WHILE READING FILE");
            e.printStackTrace();

        }
        return line;

    }

    /**
     * Reads IO activity using file in /proc path
     */


    private int getNumberFromIOLine(String line)
    {
        int spaceLocation = line.indexOf(' ');
        String number=line.substring(spaceLocation + 1);
        return Integer.parseInt(number);
    }


    /**
     * Method that will record all the metrics when called
     */


    /**
     *
     */




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