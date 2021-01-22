package io.streams;

import java.io.*;

public class Task03 {

    /*
    Со користење на I/O стримови ќе се чита датотеката 'samoglaski_izvor.txt', линија по линија, и после за секоја линија ќе го
    запише бројот на повторувања на сомогласки во истата. Резултатот се запишува во "samoglaski_destinacija.txt"
     */
    public static void samoglaski() throws IOException {
        String pathToSource = "src/io/data/samoglaski_izvor.txt";
        String pathToDestination = "src/io/data/samoglaski_destinacija.txt";
        File source = new File(pathToSource);
        File dest = new File(pathToDestination);
        dest.createNewFile(); // you can skip this line because PrintWriter will create the file for you if it does not already exist

        BufferedReader bf = null;
        PrintWriter pw = null;
        try{
            bf = new BufferedReader(new FileReader(source));
            pw = new PrintWriter(dest);

            String line = null;
            while (true){
                line = bf.readLine();
                if(line == null){
                    break;
                }
                char[] characters = line.toCharArray();
                // String[] chrs = line.split("");
                int counter = 0;
                for(char c : characters){
                    if(c == 'а' || c == 'е' || c == 'и' || c == 'о' || c == 'у'){
                        counter++;
                    }
                }
                pw.println(counter);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            // check & close
            if(bf != null){
                bf.close();
            }
            if(pw != null){
                pw.flush();
                pw.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        samoglaski();
    }

}
