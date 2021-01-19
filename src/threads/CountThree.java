package threads;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
Со помош на синхронизациските методи да се реши проблемот за определување на бројот на појавувања на бројот 3 во
огромна низа и негово запишување во глобална променлива count.

Секвенцијалното решение не е прифатливо поради тоа што трае многу долго време (поради големината на низата).
За таа цел, потребно е да се паралелизира овој процес, при што треба да се напише метода која ќе ги брои појавувањата
на бројот 3 во помал фрагмент од низата, при што резултатот повторно се чува во глобалната заедничка променлива count.

Напомена: Почетниот код е даден во почетниот код CountThree.java. Задачата да се тестира над низа од минимум 1000 елементи.
 */

class Counter extends Thread {

    public static int count = 0;

    private int[] data;

    public static Object monitor = new Object();
    public static Lock lock = new ReentrantLock();
    public static Semaphore semaphore = new Semaphore(1);

    public Counter(int[] data) {
        this.data = data;
    }

    public void count(int[] data) throws InterruptedException {
        for(int i=0; i<data.length; i++){
            if(data[i] == 3){
//                synchronized (Counter.class){
//                    count++;
//                }
//                synchronized (monitor){
//                    count++;
//                }
//                lock.lock();
//                count++;
//                lock.unlock();
                semaphore.acquire();
                count++;
                semaphore.release();
            }
        }
        // with a for each loop:
//        for (int datum : data) {
//            if (datum == 3) {
//                synchronized (Counter.class){
//                    count++;
//                }
//                synchronized (monitor){
//                    count++;
//                }
//                lock.lock();
//                count++;
//                lock.unlock();
//                semaphore.acquire();
//                count++;
//                semaphore.release();
//            }
//        }
    }

    @Override
    public void run() {
        try {
            count(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class CountThree {

    public static int NUM_RUNS = 3;

    public static void main(String[] args) throws InterruptedException {
        HashSet<Thread> threads = new HashSet<>();
        Scanner s = new Scanner(System.in);
        // total - the size of the parts from the big array
        int total=s.nextInt();

        for (int i = 0; i < NUM_RUNS; i++) {
            int[] data = new int[total];
            for (int j = 0; j < total; j++) {
                data[j] = s.nextInt();
            }
            Counter c = new Counter(data);
            threads.add(c);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println(Counter.count);
    }

}

