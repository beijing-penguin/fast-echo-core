package com.dc.im.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class FileRead {
    public static void main(String[] args) throws Exception {
        // 指定读取的行号
        int lineNumber = 12;
        // 读取文件
        File sourceFile = new File("send_.log");
        // 读取指定的行
        readAppointedLineNumber(sourceFile, lineNumber);
    }

    // 读取文件指定行。
    static void readAppointedLineNumber(File sourceFile, int lineNumber) throws IOException {
        FileReader in = new FileReader(sourceFile);
        LineNumberReader reader = new LineNumberReader(in);
        String s = reader.readLine();
        while (s != null) {
            System.out.println("当前行号为:" + reader.getLineNumber());
            reader.setLineNumber(lineNumber);
            System.out.println("更改后行号为:" + reader.getLineNumber());
            s = reader.readLine();
            System.out.println(s);
            System.exit(0);
            
        }
        reader.close();
        in.close();
    }
}
