package com.dc.im.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

public class ReadTest {
    private static String fileName = "text03.txt";
    public static void main(String[] args) throws Exception {
        continueBufferedReader_readLine();
//        for (int i = 0; i < 20; i++) {
//            useLineNumberReader_readLine();
//            useBufferedReader_readLine();
//        }
    }
    public static void useLineNumberReader_readLine() throws Exception {
        long begin = System.currentTimeMillis();
        LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
        String line = null;
        while ((line = lnr.readLine()) != null) {
            //String s = lnr.getLineNumber() + ":" + line;
            //System.out.println(s);
        }
        lnr.close();
        System.out.println("useLineNumberReader_readLine执行耗时: " + (System.currentTimeMillis() - begin));
    }
    
    
    public static void useBufferedReader_readLine() throws Exception {
        long begin = System.currentTimeMillis();
        BufferedReader reader  = new BufferedReader(new FileReader(fileName));
        String tempString = null;
        while ((tempString = reader.readLine())!=null){
            //String line = tempString;
        }
        reader.close();
        System.out.println("useBufferedReader_readLine执行耗时: " + (System.currentTimeMillis() - begin));
    }
    
    public static void continueBufferedReader_readLine() throws Exception {
        BufferedReader reader  = new BufferedReader(new FileReader(fileName));
        String tempString = null;
        while (true){
            String lineStr = reader.readLine();
            if(lineStr!=null && lineStr.length()>0) {
                System.err.println(lineStr);
            }else {
                Thread.sleep(1000);
            }
        }
    }
}
