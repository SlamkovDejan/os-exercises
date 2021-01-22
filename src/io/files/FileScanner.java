package io.files;

import java.io.File;
import java.util.concurrent.Semaphore;

/*
Да се имплементира класа FileScanner која што ќе се однесува како thread. Во класата FileScanner се чуваат податоци за :
 - патеката на директориумот што треба да се скенира
 - статичка променлива counter што ќе брои колку нишки од класата FileScanner ќе се креираат

Во класата FileScanner да се имплементираа статички методот што ќе печати информации за некоја датотека од следниот формат:
dir: C:\Users\185026\Desktop\lab1 - reshenija 4096 (dir за директориуми, апсолутна патека и големина)
file: C:\Users\Stefan\Desktop\spisok.pdf 29198 (file за обични фајлови, апсолутна патека и големина)

Дополнително да се преоптовари методот run() од класата Thread, така што ќе печати информации за директориумот за којшто
е повикан. Доколку во директориумот има други под директориуми, да се креира нова нишка од тип FileScanner што ќе ги
прави истите работи како и претходно за фајловите/директориумите што се наоѓаат во тие директориуми (рекурзивно).

На крај да се испечати вредноста на counter-от, односно колку вкупно нишки биле креирани.
 */

public class FileScanner extends Thread {

    private String fileToScan;
    //TODO: Initialize the start value of the counter
    private static Long counter = 0l;
    private static Semaphore counterSemaphore = new Semaphore(1);

    public FileScanner (String fileToScan){
        this.fileToScan=fileToScan;
        //TODO: Increment the counter on every creation of FileScanner object

        try {
            counterSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        counter++;
        counterSemaphore.release();
    }

    private static long calculateFolderSize(File folder) {
        File[] contents = folder.listFiles();
        long currentFolderSize = 0;
        for(File f : contents){ // contents[i] == f
            if(f.isFile()){
                currentFolderSize += f.length();
            } else if(f.isDirectory()) {
                currentFolderSize += calculateFolderSize(f);
            }
        }
        return currentFolderSize;
    }

    public static void printInfo(File file)  {
        String type = "";
        long size = 0;
        if(file.isDirectory()){
            type = "dir";
            size = calculateFolderSize(file);
        } else if(file.isFile()){
            type = "file";
            size = file.length();
        }
        String absolute = file.getAbsolutePath();
        System.out.printf("%s: %s %d\n", type, absolute, size);
    }

    public static Long getCounter () {
        return counter;
    }


    public void run() {

        //TODO Create object File with the absolute path fileToScan.
        File folder = new File(this.fileToScan);
        printInfo(folder);

        //TODO Create a list of all the files that are in the directory file.
        File [] files = folder.listFiles();

        for (File f : files) {
            if(f.isFile()){
                printInfo(f);
            } else if(f.isDirectory()){
                FileScanner fs = new FileScanner(f.getAbsolutePath());
                fs.start();
                try {
                    fs.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main (String [] args) {
        String FILE_TO_SCAN = "C:\\Users\\Slamkov\\Desktop\\Private Lessons\\OS\\threads\\src\\io";

        //TODO Construct a FileScanner object with the fileToScan = FILE_TO_SCAN
        FileScanner fileScanner = new FileScanner(FILE_TO_SCAN);

        //TODO Start the thread from type FileScanner
        fileScanner.start();

        //TODO wait for the fileScanner to finish
        try {
            fileScanner.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //TODO print a message that displays the number of thread that were created
        System.out.println(FileScanner.getCounter());

    }

}

