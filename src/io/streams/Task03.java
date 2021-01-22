package io.streams;

import java.io.*;

public class Task03 {

    public static void samoglaski() throws IOException {
        String pathToIzvor = "src/io/data/samoglaski.txt";
        String destination = "src/io/data/samoglaski_destinacija.txt";
        File izvor = new File(pathToIzvor);
        File dest = new File(destination);
        dest.createNewFile();

        BufferedReader bf = null;
        PrintWriter pw = null;
        try{
            bf = new BufferedReader(new FileReader(izvor));
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
