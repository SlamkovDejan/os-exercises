package threads;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

// https://www.facebook.com/groups/finkiOS/permalink/3310113482397627/

class Locks{
    public static Semaphore semaphore0 = new Semaphore(0);
    public static Semaphore semaphore1 = new Semaphore(1);
    public static Semaphore semaphore2 = new Semaphore(2);
}

class SharedResource{
    private int counter;

    public SharedResource() {
        this.counter = 0;
    }

    public void increment(){
        this.counter++;
    }

    public int getCounter() {
        return counter;
    }
}

class ThreadA extends Thread{

    private SharedResource resource;

    public ThreadA(SharedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        try {
            this.execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void execute() throws InterruptedException {
        Locks.semaphore0.acquire();
        System.out.print("A");
        Locks.semaphore1.release();


        Locks.semaphore2.acquire();
        this.resource.increment();
        Locks.semaphore2.release();
    }

}

class ThreadB extends Thread{

    private SharedResource resource;

    public ThreadB(SharedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        try {
            this.execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void execute() throws InterruptedException {
        Locks.semaphore1.acquire();
        System.out.print("B");
        Locks.semaphore0.release();


        Locks.semaphore2.acquire();
        this.resource.increment();
        Locks.semaphore2.release();
    }

}

public class CounterExampleExam {

    public static void main(String[] args) throws InterruptedException {
        for (int i=0; i<50; i++)
            main();
    }

    public static void main() throws InterruptedException {
        SharedResource resource = new SharedResource();
        HashSet<Thread> threads = new HashSet<>();

//        for(int i=0; i<5; i++){
//            SharedResource recourceA = new SharedResource();
//            threads.add(new ThreadA(recourceA));
//
//            SharedResource recourceb = new SharedResource();
//            threads.add(new ThreadB(recourceb));
//        }

        for(int i=0; i<5; i++){
            threads.add(new ThreadA(resource));
            threads.add(new ThreadB(resource));
        }

        for(Thread t: threads)
            t.start();

        for(Thread t: threads)
            t.join();

        int counter = resource.getCounter();
        System.out.println();
        System.out.println(counter);
    }

}
