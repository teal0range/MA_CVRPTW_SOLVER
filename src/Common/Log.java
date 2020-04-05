package Common;

import java.io.*;

public class Log {
    static BufferedWriter logger;
    static boolean onOff=true; // debug
    static boolean isStarted;

    public static boolean isLogging(){
        return isStarted;
    }

    public static void turnOn(){
        onOff=true;
    }

    public static void turnOff(){
        onOff=false;
    }

    public static void start(File dir, String filename, boolean toAppend){
        if (!onOff) {
            return;
        }
        logger = null;
        File file = new File(dir, filename);
        try {
            FileOutputStream fos = new FileOutputStream(file, toAppend);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            logger = new BufferedWriter(osw);
            isStarted = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void start(File file, boolean toAppend) {
        if (!onOff) {
            return;
        }
        logger = null;
        try {
            FileOutputStream fos = new FileOutputStream(file, toAppend);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            logger = new BufferedWriter(osw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void flush(){
        try {
            logger.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void end(){
        if(!onOff){
            return;
        }
        try{
            logger.flush();
            logger.close();
            isStarted=false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeln(String str){
        if (!onOff)
            return;
        try {
            logger.write(str);
            logger.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String str){
        if (!onOff)
            return;
        try {
            logger.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
