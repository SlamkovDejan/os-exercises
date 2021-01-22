package io.streams;

// http://code.finki.ukim.mk/course/102/problem/5615/

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class Task01 {

    public static void main(String[] args) throws IOException {

        String pathToSource = "src/io/data/izvor.txt";
        String pathToDestination = "src/io/data/destinacija.txt";

        File src = new File(pathToSource);
        File dst = new File(pathToDestination);
        if(!dst.exists())
            dst.createNewFile();

        RandomAccessFile raf = null;
        PrintWriter pw = null;
        try{
            raf = new RandomAccessFile(src, "r");
            pw = new PrintWriter(dst);
            long size = src.length();
            for(long i=size-1; i>=0; i--){
                raf.seek(i);
                byte b = (byte) raf.read();
                pw.write(b);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(raf != null){
                raf.close();
            }
            if(pw != null){
                pw.flush();
                pw.close();
            }
        }

    }

}
