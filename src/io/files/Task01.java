package io.files;

/*
Да се напише Java програма која ќе прикаже колкава е просечната големина на датотеките со екстензија .txt во именик
зададен како аргумент на методата.
 */


import java.io.File;

public class Task01 {

    private static long sumOfTxtFiles(String dirPath){
        File folder = new File(dirPath);
        File[] contents = folder.listFiles();
        long sumSize = 0;
        for(File f : contents){
            if(f.isFile() && f.getName().endsWith(".txt")){
                sumSize += f.length();
            } else if(f.isDirectory()){
                sumSize += sumOfTxtFiles(f.getAbsolutePath());
            }
        }
        return sumSize;
    }

    private static int numOfTxtFiles(String dirPath){
        File folder = new File(dirPath);
        File[] contents = folder.listFiles();
        int numFiles = 0;
        for(File f : contents){
            if(f.isFile() && f.getName().endsWith(".txt")){
                numFiles++;
            } else if(f.isDirectory()){
                numFiles += numOfTxtFiles(f.getAbsolutePath());
            }
        }
        return numFiles;
    }

    private static double averageSize(String dirPath){
        int numTxt = numOfTxtFiles(dirPath);
        return numTxt == 0 ? 0 : sumOfTxtFiles(dirPath) / (double) numTxt;
    }

    public static void main(String[] args) {
        String dirPath = "C:\\Users\\Slamkov\\Desktop\\Private Lessons\\OS\\threads\\src\\io\\data";
        System.out.println(averageSize(dirPath));
    }

}
