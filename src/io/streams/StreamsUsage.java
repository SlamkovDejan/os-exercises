package io.streams;

import java.io.*;

public class StreamsUsage {

    public static void main(String[] args) throws IOException {

        // streams -> wrappers over some data source

        // 'InputStream' and 'OutputStream' are abstract classes and serve as a base class for the more exact streams
        // this means that an object can't be instantiated from them (abstract)
        InputStream is;
        OutputStream os;
        baseStreamClassesUsage();

        // stream classes tailored for a certain type of source, ex. byte array, file
        ByteArrayInputStream bais;
        ByteArrayOutputStream baos;
        FileInputStream fis;
        FileOutputStream fos;
        sourceSpecificStreamsUsage();

        // special streams wrapping other streams to offer something more
        // ex. 'DataInputStream' wraps another stream and let's you read primitive types from it: .readInt(); readFloat(); ...
        // ex. 'Buffered(Input/Output)Stream' wraps another stream for more efficient reading/writing
        DataInputStream dis;
        DataOutputStream dos;
        BufferedInputStream bis;
        BufferedOutputStream bos;
        specialStreamsUsage();


        // unlike the streams, the readers/writers work with characters and string instead of bytes
        // similar to 'InputStream' and 'OutputStream', 'Reader' and 'Writer' are abstract classes that every reader/writer inherits from
        Reader r;
        Writer w;
        baseReaderWriterUsage();

        // reader/writer classes tailored for a certain type of source, ex. file, stream
        FileReader fr;
        FileWriter fw;
        InputStreamReader isr;
        OutputStreamWriter osw;
        sourceSpecificReaderWriterUsage();

        // special readers/writers wrapping other readers/writers to offer something more
        // ex. 'Buffered(Reader/Writer)' wraps another reader/writer for more efficient reading/writing
        // ex. 'PrintWriter' offers the methods similar to the object 'System.out'
        // ex. 'RandomAccessFile' offers handling of large files
        BufferedReader br;
        BufferedWriter bw;

        PrintWriter pw;
        RandomAccessFile acf;
        specialReaderWriterUsage();

        recommendedUsage();
    }

    private static void recommendedUsage() throws IOException {
        // READING
        // for reading, i decide which class to use by two scenarios:
        // line by line, text: BufferedReader (I USE THIS THE MOST)
        BufferedReader bf = null;
        // specific type: DataInputStream
        // i use this when i know the type of the data i'm reading (it's not just text)
        DataInputStream dis = null;
        try{
            bf = new BufferedReader(new FileReader("src/io/data/dummy_read.txt"));
            String line = null;
            // going throuh the file; line by line; 'readLine()' returns null when the end of file is reached
            while((line = bf.readLine()) != null){
                System.out.println(line);
            }
//            while (true){
//                line = bf.readLine();
//                if(line == null)
//                    break;
//                System.out.println(line);
//            }

            dis = new DataInputStream(new FileInputStream("src/io/data/dummy_read.txt"));
            dis.readInt();
            dis.readDouble();
            dis.readLong();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(bf != null){
                bf.close();
            }
            if(dis != null){
                dis.close();
            }
        }

        // WRITING
        // I always use 'PrintWriter' because it's similar to 'System.out'
        PrintWriter pw = null;
        try{
            pw = new PrintWriter("src/io/data/dummy_read.txt");
            pw.println("whatever");
            pw.println(2);
            pw.println(2.0);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(pw != null){
                pw.flush();
                pw.close();
            }
        }

        // i recommend using the previous mentioned classes for i/o manipulation, unless the task says otherwise
    }

    // demonstrates the usage of the methods of 'InputStream' and 'OutputStream'
    private static void baseStreamClassesUsage() throws IOException {
        InputStream in = null;
        OutputStream out = null;

        // the following is not allowed (abstract)
        // in = new InputStream();
        // out = new OutputStream();

        try {
            // the keyboard (as a source) is wrapped in a 'InputStream'
            in = System.in;

            // returns one byte from the stream
            byte b = (byte) in.read(); // returns -1 if the streams is at it's end

            // it will read at max 50 bytes because the buffer can't hold anymore than that (buffer.length == 50)
            byte[] buffer = new byte[50];
            int numBytesRead = in.read(buffer); // reads bytes from the stream into the buffer and returns how many bytes have been read
            if(numBytesRead == -1){
                System.out.println("No bytes were read!");
            } else {
                // some bytes were read
                // how many? --> 'numBytesRead'
                for(int i=0; i<numBytesRead; i++) {
                    byte byteFromBuffer = buffer[i];
                }
            }

            // skips the next 5 bytes (no going back)
            in.skip(5);

            // returns the number of bytes left to be read from the stream
            long bytesLeft = in.available();


            // the screen (as a source) is wrapped in a 'OutputStream'
            out = System.out;

            // writes one byte (a character is one byte in memory)
            out.write((byte) 'c');
            // writes multiple bytes
            out.write(buffer);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(in != null){
                in.close(); // we close the streams to release the source
            }
            if(out != null){
                // when we write to a stream, it does not automatically write the data to the source (it's still in memory)
                // that's why we use the 'flush' method, so that we can force the stream to write the data to the source
                out.flush();
                out.close();
            }
        }

    }

    // demonstrates the usage of the source specific stream classes
    private static void sourceSpecificStreamsUsage() throws IOException {
        // for polymorphism purposes (will explain bellow)
        InputStream is = null;
        OutputStream os = null;

        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;

        // byte array source
        try{
            // the argument is the size of the byte array on which this stream will write
            // in this case it will create a byte array with 26 elements; byteArray = new byte[26];
            baos = new ByteArrayOutputStream(26);
            // filling the source (byteArray) with the alphabet
            // one character is one byte
            for(int i=0; i<26; i++){
                char letter = (char) ('a' + i);
                baos.write((byte) letter); // one byte
            }
            baos.flush(); // we must flush so that the stream actually writes the bytes on the array
            byte[] source = baos.toByteArray(); // retrieves the source, which is the byte array of 26 elements

            // using the same byte array as the source for the input stream
            bais = new ByteArrayInputStream(source);

            // has the same methods as the abstract class
            // reads from 'source', which is the byte array from the output stream
            // so the first byte read will be the letter 'a', the second the letter 'b', etc.
            // we can directly cast the byte to char knowing that the bytes in the array (stream) are actually characters
            char a = (char) bais.read();
            char b = (char) bais.read();

            // of course i can do this
            // reference of 'OutputStream' holds an object from 'ByteArrayOutputStream' (polymorphism): ByteArrayOutputStream extends OutputStream
            // the reference uses the source from the object it references, in this case a byte array
            os = new ByteArrayOutputStream(10);
            // os.toByteArray(); -- can't use this method because it's specific to 'ByteArrayOutputStream', not 'OutputStream'

            // the next line will throw an exception because the byte array 'source' is still taken by another stream ('bais');
            // no 'close()' has been called on 'bais' previous to this line
            // to avoid the exception we need to change the source in the constructor with another byte array, which is not taken by some stream
            is = new ByteArrayInputStream(source);

            os.write((byte) '0'); // writes to the byte array with 10 elements

            os.close();
            is.close();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (bais != null) {
                bais.close();
            }
            if(is != null){
                is.close();
            }
            if(baos != null){
                baos.flush();
                baos.close();
            }
            if(os != null){
                os.flush();
                os.close();
            }
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;

        // file source
        try{
            String pathToOutputFile = "src/io/data/dummy_write.txt";
            File outputFileObject = new File(pathToOutputFile);
            
            // there are multiple constructors of the 'FileOutputStream' class
            // one takes the direct path to the file, the other takes a 'File' object as an argument
            fos = new FileOutputStream(pathToOutputFile);
            fos = new FileOutputStream(outputFileObject);

            // the previous two constructors will delete the previous contents (if there's any) of the file sent through the constructor
            // and will start fresh. If we don't want that behavior, we can set a second argument of the constructor named 'append'
            // which tells the stream if we want to continue writing (append) on the file, or start fresh
            // append == true => we want to continue writing
            // append == false => we want to start fresh (the default behavior)
            fos = new FileOutputStream(pathToOutputFile, true);
            fos = new FileOutputStream(outputFileObject, false); // has the same effect as: 'fos = new FileOutputStream(outputFileObject);'

            // Has the same methods as the parent 'OutputStream' class: .write(byte); .write(byte[]); .flush(); ...
            fos.write((byte) 'a');
            fos.write((byte) 'b');


            String pathToInputFile = "src/io/data/dummy_read.txt";
            File inputFileObject = new File(pathToInputFile);

            // has similar constructors as 'FileOutputStream'
            // there are no constructors with 'append' because that's only relevant with writing on a file
            fis = new FileInputStream(pathToInputFile);
            fis = new FileInputStream(inputFileObject);

            // Has the same methods as the parent 'InputStream' class: .read(); .read(byte[]); .skip(n); ...
            char firstCharacterFromFile = (char) fis.read();
            fis.skip(1); // REMINDER: one byte = one character
            char thirdCharacterFromFile = (char) fis.read();

            // reading all the bytes from the stream (file)
            byte b;
            while ((b = (byte) fis.read()) != -1){ // traverse until we get -1, which means end of stream
                // do something with the byte
                char characterFromFile = (char) b;
            }
            // other way
            while (true){
                b = (byte) fis.read(); // this will return -1 on the first iteration. Why?
                if(b == -1){ // means we got to the end of the stream --> break
                    break;
                }
                // do something with the byte
                char characterFromFile = (char) b;
            }

            // of course i can do this
            // reference of 'OutputStream' holds an object from 'FileOutputStream' (polymorphism): FileOutputStream extends OutputStream
            // but first i must change the data source (file) because the previous are still taken by other streams
            pathToInputFile = "path/to/some/other/file/input.txt";
            pathToOutputFile = "path/to/some/other/file/output.txt";
            os = new FileOutputStream(pathToOutputFile);
            is = new FileInputStream(pathToInputFile);

            // using the 'InputStream' reference (who points to a FileInputStream) to read from the file
            char firstCharacterFromOtherFile = (char) is.read();
            is.skip(1);
            char thirdCharacterFromOtherFile = (char) is.read();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(fis != null){
                fis.close();
            }
            if(is != null){
                is.close();
            }
            if(fos != null){
                fos.flush();
                fos.close();
            }
            if(os != null){
                os.flush();
                os.close();
            }
        }

    }

    // demonstrates the usage of the special stream classes
    private static void specialStreamsUsage() throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            // IMPORTANT: the special input streams wrap another input stream, i.e. they take a 'InputStream' reference in the constructor
            // meaning that every class that inherits from 'InputStream' can be sent through the constructor of the special stream
            bis = new BufferedInputStream(new FileInputStream("src/io/data/dummy_read.txt"));
            bis = new BufferedInputStream(new ByteArrayInputStream(new byte[26]));
            bis = new BufferedInputStream(new DataInputStream(new FileInputStream("src/io/data/dummy_read.txt")));

            // Has the same methods as the parent 'InputStream': .read(); .read(byte[]); skip(n); ...
            byte b = (byte) bis.read(); // one byte from the underlying stream (file, array, another special, ...); -1 if the stream is at it's end

            // same goes for the special output streams
            bos = new BufferedOutputStream(new FileOutputStream("src/io/data/dummy_write.txt", false));
            bos = new BufferedOutputStream(new ByteArrayOutputStream(26));
            bos = new BufferedOutputStream(new BufferedOutputStream(new ByteArrayOutputStream(26)));

            // Has the same methods as the parent 'OutputStream': .write(byte); .write(byte[]); ...
            bos.write('c'); // writes the byte to the underlying stream (file, array, another special, ...)

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(bis != null){
                bis.close();
            }
            if(bos != null){
                bos.flush();
                bos.close();
            }
        }

        DataInputStream dis = null;
        DataOutputStream dos = null;

        try{
            // The constructor is the same as the buffered special stream
            dos = new DataOutputStream(new FileOutputStream("src/io/data/dummy_write.txt"));

            // special methods provided by DataOutputStream (writes to the underlying stream, in this case a file stream)
            // previously we only had the opportunity to only write bytes, so we needed to convert the types into bytes
            // this class does that for us
            dos.writeBoolean(true);
            dos.writeChar('2'); // we can do this with 'write(byte)'
            dos.writeUTF("String");
            dos.writeDouble(2.0);
            dos.writeInt(2);


            // The constructor is the same as the buffered special stream
            dis = new DataInputStream(new FileInputStream("src/io/data/dummy_read.txt"));
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream("src/io/data/dummy_read.txt")));

            // special methods provided by DataInputStream (reads from the underlying stream, in this case a file stream)
            // previously we only had the opportunity to only read bytes, which we later convert into types
            // this class does that for us
            // throws 'EOFException' if the end of the stream has been reached
            dis.readBoolean();
            dis.readChar();
            dis.readDouble();
            dis.readFloat();
            dis.readInt();
            dis.readLong();
            dis.readUTF(); // string

        } catch (EOFException e){
            e.printStackTrace();
        } finally {
            if(dis != null){
                dis.close();
            }
            if(dos != null){
                dos.flush();
                dos.close();
            }
        }

    }

    // demonstrates the usage of the base reader/writer classes
    private static void baseReaderWriterUsage() throws IOException {
        Reader r = null;
        Writer w = null;

        // this is not allowed (abstract)
        // r = new Reader();
        // w = new Writer();

        try{
            w = new OutputStreamWriter(System.out);
            char[] charArr = new char[]{'D', 'e', 'k', 'i'};
            w.write('a');
            w.write(charArr);
            w.write("Deki");


            r = new InputStreamReader(System.in);
            char[] buffer = new char[5]; // char[] instead of byte[]
            int numCharacterRead = r.read(buffer);
            if(numCharacterRead == -1){
                System.out.println("No characters read!");
            }
            else{
                for(int i=0; i<numCharacterRead; i++){
                    System.out.print(buffer[i]);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(r != null){
                r.close();
            }
            if(w != null){
                w.flush();
                w.close();
            }
        }
    }

    // demonstrates the usage of the source specific reader/writer classes
    private static void sourceSpecificReaderWriterUsage() throws IOException {
        FileReader fr = null;
        FileWriter fw = null;

        try {
            String pathToOutputFile = "src/io/data/dummy_write.txt";
            File outputFileObject = new File(pathToOutputFile);

            // the same constructors as the 'FileOutputStream' class
            fw = new FileWriter(pathToOutputFile);
            fw = new FileWriter(pathToOutputFile, true);
            fw = new FileWriter(outputFileObject);
            fw = new FileWriter(outputFileObject, true);

            // same methods as 'Writer'
            char[] charArr = new char[]{'D', 'e', 'k', 'i'};
            // writes to the file
            fw.write('a');
            fw.write(charArr);
            fw.write("Deki");

            String pathToInputFile = "src/io/data/dummy_read.txt";
            File inputFileObject = new File(pathToInputFile);

            fr = new FileReader(pathToInputFile);
            fr = new FileReader(inputFileObject);

            char firstCharacterInFile = (char) fr.read();
            fr.skip(1);
            char secondCharacterInFile = (char) fr.read(); // .read() returns -1 if the end of the source has been reached

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(fr != null){
                fr.close();
            }
            if(fw != null){
                fw.flush();
                fw.close();
            }
        }

        InputStreamReader isr = null;
        OutputStreamWriter osw = null;

        try{
            // the  constructor takes a reference to 'OutputStream' which means that every class that extends 'OutputStream'
            // can be the source of the 'OutputStreamWriter'
            osw = new OutputStreamWriter(new FileOutputStream("src/io/data/dummy_write.txt"));
            osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("src/io/data/dummy_write.txt")));

            // same methods as 'Writer': write(char); write(char[]); write(string)
            osw.write("string"); // writes to the underlying stream

            isr = new InputStreamReader(new FileInputStream("src/io/data/dummy_read.txt"));
            isr = new InputStreamReader(new ByteArrayInputStream(new byte[26]));

            // same methods as 'Reader': read(); read(char[])
            char c = (char) isr.read(); // reads from the underlying stream

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(isr != null){
                isr.close();
            }
            if(osw != null){
                osw.flush();
                osw.close();
            }
        }
    }

    // demonstrates the usage of the special reader/writer classes
    private static void specialReaderWriterUsage() throws IOException {
        BufferedReader br = null;
        BufferedWriter bw = null;

        try{
            // similar like the special input streams, the special readers wrap another reader, i.e. a class that inherits from 'Reader'
            br = new BufferedReader(new FileReader("src/io/data/dummy_read.txt"));
            // the same as the line above
            br = new BufferedReader(new InputStreamReader(new FileInputStream("src/io/data/dummy_read.txt")));
            br = new BufferedReader(new InputStreamReader(System.in));

            br.read(); // one byte; returns -1 if the end of the source has been reached
            br.readLine(); // one line; returns null if the end of the source has been reached

            // similar like the special output streams, the special writers wrap another writer, i.e. a class that inherits from 'Writer'
            bw = new BufferedWriter(new FileWriter("src/io/data/dummy_read.txt", true));
            bw = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("src/io/data/dummy_read.txt", false))));

            // same methods as 'Writer': .write(char); .write(char[]); write(string)
            bw.write('c');
            bw.write("string");
            bw.write(new char[]{'2', '3'});

            // polymorphism
            Reader r = br;
            r.read(); // uses the '.read()' from the 'br' object
            // r.readLine(); -- CAN'T USE THIS; SPECIFIC TO 'BUFFERED READER'
            Writer w = bw;
            w.write('c'); // uses the '.write()' from the 'bw' object

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(br != null){
                br.close();
            }
            if(bw != null){
                bw.flush();
                bw.close();
            }
        }

        PrintWriter pw = null;

        try{
            boolean append = false;
            // 'PrintWriter' has many constructors:
            // with writer as argument
            pw = new PrintWriter(new FileWriter("src/io/data/dummy_write.txt", append));
            // with output stream as argument
            pw = new PrintWriter(new FileOutputStream("src/io/data/dummy_write.txt", append));
            // with file path as argument
            pw = new PrintWriter("src/io/data/dummy_write.txt"); // append = false
            // with file object as argument
            pw = new PrintWriter(new File("src/io/data/dummy_write.txt")); // append = false
            // the previous four constructors construct a writer to the same file source, but with a different constructor
            // the writer (as constructed with the previous constructors) will erase the contents of the file source (if there's any) => append = false
            // if we want to write at the end of the file (not erase), we need to use one of the first two constructors and set (append = true)
            // of course, the source can be other than a file, by using other output streams with the second constructor or
            // other writers with the first constructor

            // a print writer object is very similar to the 'System.out' object
            pw.println("string");
            pw.printf("%s %s\n", "string1", "sting2");
            pw.println(2);
            pw.println(true);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(pw != null){
                pw.flush();
                pw.close();
            }
        }

        RandomAccessFile acf = null;

        try{
            String filePath = "src/io/data/dummy_read.txt";
            File fileObject = new File(filePath);

            acf = new RandomAccessFile(filePath, "r"); // reading
            acf = new RandomAccessFile(fileObject, "w"); // writing
            acf = new RandomAccessFile(fileObject, "rw"); // reading & writing

            // has the methods similar to 'DataInputStream'
            acf.readBoolean();
            acf.readDouble();
            acf.readUTF();

            // has similar methods to 'DataOutputStream'
            acf.writeBoolean(true);
            acf.writeInt(2);
            acf.writeUTF("string");

            // this is why this class is special:
            // when we use other streams/readers/writers to read/write to a file, most of them load the entire file into memory
            // What if the file is bigger than the RAM memory of the machine? That's where RAF comes in play
            // RAF has a mechanism that does not read the whole file into memory, but gives you random access to it
            acf.seek(256); // leaps 256 bytes forward from the beginning of the file, meaning the next read/write operation will start from byte 257
            acf.read(); // reads byte 257

            acf.seek(10); // leaps 10 bytes forward from the beginning of the file
            acf.read(); // reads byte 11

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(acf != null){
                acf.close();
            }
        }

    }

}
