package io.streams;


import java.io.*;

public class Task02 {

    /*
    Потребно е да ја имплементирате функцијата manage(String in, String out) која врши организација на текстуалните
    датотеки (*.dat) од именикот in според нивните привилегии на следниот начин:

    Доколку датотеката има привилегии за запишување тогаш таа треба да се премести во out именикот.
    При преместувањето, во конзола испечатете pomestuvam и апсолутното име на датотеката која се копира.

    Доколку датотеката нема привилегии за запишување тогаш нејзината содржина додадете ја на крај од датотеката
    writable-content.txt во resources именикот. При додавањето, во конзола испечатете dopisuvam и апсолутното име на датотеката. Сметајте дека овие датотеки може да бидат многу поголеми од физичката меморија на компјутерот.

    Доколку датотеката е скриена (hidden), тогаш избришете ја од in именикот, и во конзола испечатете zbunet sum и
    апсолутното име на датотеката.

    Доколку in именикот не постои, испечатете на екран ne postoi.
    Доколку out именикот веќе постои, избришете ја неговата содржина. Претпоставете дека во out именикот има само датотеки.
     */
    public static void manage(String in, String out, String resources) throws IOException {
        File inFolder = new File(in);
        if(!inFolder.exists()){
            System.out.println("ne postoi");
            return;
        }
        File outFolder = new File(out);
        if(outFolder.listFiles().length != 0){
            // remove contents from outFolder
            File[] contentsInOut = outFolder.listFiles();
            for (File f : contentsInOut){
                f.delete();
            }
        }

        File writeableFile = new File(resources, "writable-content.txt");
        RandomAccessFile raf = null;
        try{
            raf = new RandomAccessFile(writeableFile, "rw");
            File[] contents = inFolder.listFiles();
            for(File f : contents){
                if(f.isFile() && f.getName().endsWith(".dat")){
                    if(f.isHidden()){
                        System.out.println("zbunet sum");
                        f.delete();
                    }
                    else if(f.canWrite()) {
                        System.out.println("pomestuvam " + f.getAbsolutePath());
                        File destination = new File(outFolder, f.getName());
                        f.renameTo(destination);
                    } else {
                        System.out.println("dopishuvam " + f.getAbsolutePath());
                        raf.seek(writeableFile.length());
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(f);
                            while (true) {
                                int b = fis.read();
                                if (b == -1) {
                                    break;
                                }
                                raf.write(b);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        } finally {
                            if(fis != null){
                                fis.close();
                            }
                        }
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

    /*
    Copies all .txt files which are larger than size (in bytes) from the from directory into the to directory.
    If the from directory does not exist, you should write "Does not exist" and if to does not exist you need to create it.
     */
    public static void copyLargeTextFiles(String from, String to, long size) throws IOException {
        File fromFolder = new File(from);
        if(!fromFolder.exists()){
            System.out.println("does not exist");
            return;
        }
        File toFolder = new File(to);
        if(!toFolder.exists()){
            toFolder.mkdirs();
        }

        File[] contents = fromFolder.listFiles();
        for(File f : contents){
            if(f.isFile() && f.getName().endsWith(".txt") && f.length() > size){
                // COPY
                File copyOfFile = new File(toFolder, f.getName());
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

    /*
    Филтрирај ги сите датотеки кои имаат големина поголема од 50KB и се наоѓаат во директориумот со патека 'folderPath'
     */
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

    /*
    Измини ги рекурзивно сите подименици на именикот со патека 'folderPath' и креирај .txt документ чие име е името
    на тековниот именик каде што се креира документот.
     */
    public static void createTxt(String folderPath) throws IOException {
        File folder = new File(folderPath);
        File file = new File(folder.getAbsolutePath(), folder.getName() + ".txt");
        // File file = new File(folderPath, folder.getName() + ".txt"); // the same as the previous line
        file.createNewFile();

        File[] contents = folder.listFiles();
        for(File f : contents){
            if(f.isDirectory()){
                createTxt(f.getAbsolutePath());
            }
        }

    }

}
