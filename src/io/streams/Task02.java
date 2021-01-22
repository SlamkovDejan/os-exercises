package io.streams;


import java.io.*;

public class Task02 {

    // http://code.finki.ukim.mk/course/102/problem/5719/
    public static void manage(String in, String out, String resources) throws IOException {
        File inFile = new File(in);
        if(!inFile.exists()){
            System.out.println("ne postoi");
            return;
        }
        File outFile = new File(out);
        if(!(outFile.listFiles().length == 0)){
            // remove contets from outFile
            File[] contentsInOut = outFile.listFiles();
            for (File f : contentsInOut){
                f.delete();
            }
        }

        File writeable = new File(resources, "writable-content.txt");
        RandomAccessFile raf = null;
        try{
            raf = new RandomAccessFile(writeable, "rw");
            File[] contentsInIn = inFile.listFiles();
            for(File f : contentsInIn){
                if(f.isFile() && f.getName().endsWith(".dat")){
                    if(f.isHidden()){
                        System.out.println("zbunet sum");
                        f.delete();
                    }
                    else if(f.canWrite()) {
                        System.out.println("pomestuvam " + f.getAbsolutePath());
                        f.renameTo(outFile);
                    } else {
                        System.out.println("dopishuvam " + f.getAbsolutePath());
                        FileInputStream fis = new FileInputStream(f);
                        raf.seek(f.length());
                        while (true){
                            int b = fis.read();
                            if(b == -1){
                                break;
                            }
                            raf.write(b);
                        }
                        fis.close();
                    }

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(raf != null){
                raf.close();
            }
        }
    }

    // http://code.finki.ukim.mk/course/102/problem/5718/
    public static void copyLargeTextFiles(String from, String to, long size) throws IOException {
        File fromFile = new File(from);
        if(!fromFile.exists()){
            System.out.println("does not exist");
            return;
        }
        File toFile = new File(to);
        if(!toFile.exists()){
            toFile.mkdirs();
        }

        File[] contents = fromFile.listFiles();
        for(File f : contents){
            if(f.isFile() && f.getName().endsWith(".txt") && f.length() > size){
                // COPY
                File copyOfFile = new File(toFile, f.getName());
                BufferedReader bf = null;
                PrintWriter pw = null;
                try{
                    bf = new BufferedReader(new FileReader(f));
                    pw = new PrintWriter(copyOfFile);

                    while (true){
                        String line = bf.readLine();
                        if(line == null) {
                            break;
                        }
                        pw.println(line);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    if(bf != null){
                        bf.close();
                    }
                    if(pw != null){
                        pw.flush();
                        pw.close();
                    }
                }
            }
        }
    }

    public static void filter(String folderPath){
        File folder = new File(folderPath);

        File[] contents = folder.listFiles();
        // 1KB = 1000B
        for(File f : contents){
            if(f.isFile() && f.length() > (50 * 1000)){
                System.out.println(f.getAbsolutePath());
            } else if(f.isDirectory()){
                filter(f.getAbsolutePath());
            }
        }
    }

    public static void createTxt(String folderPath) throws IOException {
        File folder = new File(folderPath);
        File file = new File(folder.getAbsolutePath(), folder.getName() + ".txt");
        file.createNewFile();

        File[] contents = folder.listFiles();
        for(File f : contents){
            if(f.isDirectory()){
                createTxt(f.getAbsolutePath());
            }
        }

    }

}
