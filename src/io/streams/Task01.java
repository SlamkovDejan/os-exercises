package io.streams;

/*
Да се напише Java програма која со користење на I/O стримови ќе ја прочита содржината на датотеката prevrteno_izvor.txt,
а потоа нејзината содржина ќе ја испише превртена во празната датотека prevrteno_destinacija.txt.
Читањето и запишувањето реализирајте го со стримови кои работат бајт по бајт.

Пример:
prevrteno_izvor.txt                   prevrteno_destinacija.txt
Оперативни системи          иметсис инвитарепО
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class Task01 {

    public static void main(String[] args) throws IOException {

        String pathToSource = "src/io/data/prevrteno_izvor.txt";
        String pathToDestination = "src/io/data/prevrteno_destinacija.txt";

        File src = new File(pathToSource);
        File dst = new File(pathToDestination);
        if(!dst.exists())
            dst.createNewFile();

        RandomAccessFile raf = null;
        PrintWriter pw = null;
        try{
            raf = new RandomAccessFile(src, "r");
            pw = new PrintWriter(dst); // PrintWriter creates the file if it does not exist
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
