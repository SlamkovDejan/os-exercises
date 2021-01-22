package io.files;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileUsage {

    private static File createFileObject(String parentDirPath, String name){
        // FILE: 'path/to/parent' + '/' + 'file.txt' => 'path/to/parent/file.txt'
        // FOLDER: 'path/to/parent' + '/' + 'dir' => 'path/to/parent/dir'
        String path = parentDirPath + "/" + name;
        System.out.println("Creating a file object for: " + path);

        // multiple ways of creating a File object (depending on the information given)
        File file = new File(path);

//        File file = new File(parentDirPath, name);

//        File parentDir = new File(parentDirPath);
//        File file = new File(parentDir, name);

        return file;
    }

    private static void move(File old, String destinationFolderPath, String newName){
        if(!old.exists()){
            System.out.println("Source (old) does not exists!");
            return;
        }
        File parent = new File(destinationFolderPath);
        parent.mkdirs();

        File destination = createFileObject(destinationFolderPath, newName);
        boolean transferSuccessful = old.renameTo(destination);
        if(transferSuccessful) {
            System.out.printf("File (%s) has been moved to: %s\n", old.getAbsolutePath(), destination.getAbsolutePath());
        } else {
            System.out.printf("File (%s) was NOT moved successfully to: %s\n", old.getAbsolutePath(), destination.getAbsolutePath());
        }
    }

    public static void main(String[] args) throws IOException {
        fileManipulation();

        // folderManipulation();
    }

    private static void fileManipulation() throws IOException {
        // FILE: 'src/io/data/deki.txt'
        String parentDirPath = "src/io/data";
        String fileName = "deki.txt";

        File myFile = createFileObject(parentDirPath, fileName);

        // BASIC OPERATIONS

        if(!myFile.exists()){
            System.out.println("Creating file: " + myFile.getName());
            boolean successfullyCreated = myFile.createNewFile();
            if(successfullyCreated) {
                System.out.println("File created successfully");
            } else {
                System.out.println("File was NOT created");
                return;
            }
        } else {
            System.out.println("File already exists: " + myFile.getAbsolutePath());
        }

        boolean r = myFile.canRead();
        boolean w = myFile.canWrite();
        boolean x = myFile.canExecute();

        String parentPath = myFile.getParent();
        File parent = myFile.getParentFile();

        boolean hidden = myFile.isHidden();
        boolean isFile = myFile.isFile();
        boolean isDir = myFile.isDirectory();

        long lastModified = myFile.lastModified();
        Date date = new Date(lastModified);

        long bytes = myFile.length();

        printInfo(myFile);

        // OTHER OPERATIONS

        // changing permissions
        boolean successfullySetRPerm = myFile.setReadable(true);
        boolean successfullySetWPerm = myFile.setWritable(false);
        boolean successfullySetXPerm = myFile.setExecutable(false);
        // the same effect as the previous 3 lines
        boolean successfullySet = myFile.setReadOnly(); // r--

        // deleting a file
        boolean successfulDeletion = myFile.delete();
        if(successfulDeletion) {
            System.out.printf("File (%s) was deleted successfully!\n", myFile.getAbsolutePath());
        } else {
            System.out.printf("File (%s) was NOT deleted successfully\n", myFile.getAbsolutePath());
        }

         // moving a file (CUT-like operation)
        String destinationFolderPath = "src/io";
        String newName = "deki_new.txt";
//         String newName = myFile.getName();
        move(myFile, destinationFolderPath, newName);
    }

    private static void folderManipulation() throws IOException {
        // FOLDER: 'C:\Users\Slamkov\Desktop\Private Lessons\OS\threads\src\io\data\myFolder'
        String parentDirPath = "src/io/data";
        String folderName = "myFolder";

        File myFolder = createFileObject(parentDirPath, folderName);

        // BASIC OPERATIONS

        if(!myFolder.exists()){
            System.out.println("Creating folder: " + myFolder.getName());
            boolean successfullyCreated = myFolder.mkdir();
            // boolean successfullyCreated = myFolder.mkdirs();
            if(successfullyCreated) {
                System.out.println("Folder created successfully");
            } else {
                System.out.println("Folder was NOT created");
                return;
            }
        } else {
            System.out.println("Folder already exists: " + myFolder.getAbsolutePath());
        }

        boolean r = myFolder.canRead();
        boolean w = myFolder.canWrite();
        boolean x = myFolder.canExecute();

        String parentPath = myFolder.getParent();
        File parent = myFolder.getParentFile();

        boolean hidden = myFolder.isHidden();
        boolean isFile = myFolder.isFile();
        boolean isDir = myFolder.isDirectory();

        long lastModified = myFolder.lastModified();

        // long bytes = myFolder.length(); -- DOES NOT WORK ON DIRECTORY
        long bytes = calculateFolderSize(myFolder);

        printInfo(myFolder);

        // OTHER OPERATIONS

        // changing permissions
        boolean successfullySetRPerm = myFolder.setReadable(true);
        boolean successfullySetWPerm = myFolder.setWritable(false);
        boolean successfullySetXPerm = myFolder.setExecutable(false);
        // the same effect as the previous 3 lines
        boolean successfullySet = myFolder.setReadOnly(); // r--

        // deleting a folder
        if (myFolder.listFiles().length == 0) {
            // this method only works if the directory is empty
            boolean successfulDeletion = myFolder.delete();
            if (successfulDeletion) {
                System.out.printf("Empty folder (%s) was deleted successfully!\n", myFolder.getAbsolutePath());
            } else {
                System.out.printf("Empty folder (%s) was NOT deleted successfully\n", myFolder.getAbsolutePath());
            }
        } else {
            deleteFolderRecursively(myFolder);
        }

        // moving a folder (CUT-like operation)
        String destinationFolderPath = "src/io/data/deep"; // 'deep' dir does not exist (.mkdirs() in move())
        String newName = "myFolder_new";
        // String newName = myFolder.getName();
        move(myFolder, destinationFolderPath, newName);
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

    private static void deleteFolderRecursively(File myFolder) {
        File[] contents = myFolder.listFiles();
        for(File f : contents) {
            if(f.isFile()){
                f.delete();
            } else if(f.isDirectory()) {
                deleteFolderRecursively(f);
            }
        }
        myFolder.delete();
    }

    private static void printInfo(File f){
//        String permissions = "";
//        if(f.canRead())
//            permissions += "r";
//        else
//            permissions += "-";
//        if(f.canWrite())
//            permissions += "w";
//        else
//            permissions += "-";
//        if(f.canExecute())
//            permissions += "x";
//        else
//            permissions += "-";

        String permissions = f.canRead() ? "r" : "-";
        permissions += f.canWrite() ? "w": "-";
        permissions += f.canExecute() ? "x": "-";

        long sizeInBytes = f.isDirectory() ? calculateFolderSize(f) : f.length();
        // 1KB = 1 000B
        long sizeInKB = sizeInBytes / 1_000;
        // 1MB = 1 000 000B
        long sizeInMB = sizeInBytes / 1_000_000;

        String type = f.isFile() ? "FILE" : "DIRECTORY";

        Date dateLastModified = new Date(f.lastModified());

        System.out.println("FILE INFO:");
        System.out.printf(
                "\tPath: %s\n\tType: %s\n\tPermissions: %s\n\tIs hidden: %s\n\tLast modified: %s\n\tSize in bytes: %d\n\tSize in KB: %d\n\tSize in MB: %d\n",
                f.getAbsolutePath(), type, permissions, f.isHidden(), dateLastModified.toString(), sizeInBytes, sizeInKB, sizeInMB
        );
    }

}
