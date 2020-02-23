package com.verdy.personalassistant.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class AuxLogWriter {
    private static BufferedWriter bufferedLogWriter;

    public static void writeToLog(String toWrite){
        if(MApp.logFile != null){
         createBufferedLogWriter();
         writeToLogAndClose(toWrite);
        }
    }

    private static void createBufferedLogWriter(){
        try {
            FileWriter logWriter = new FileWriter(MApp.logFile, true);
            bufferedLogWriter = new BufferedWriter(logWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToLogAndClose(final String toWrite){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    processWriting(toWrite);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }

    private static void processWriting(String toWrite) throws IOException{
        bufferedLogWriter.newLine();
        bufferedLogWriter.write("# Called on " + getCurrentHour() + " ");
        bufferedLogWriter.write(toWrite);
        bufferedLogWriter.close();
    }



    private static String getCurrentHour(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }

}
