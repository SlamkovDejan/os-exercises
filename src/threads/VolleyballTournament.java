package threads;

// https://imgur.com/a/2WX45MO?fbclid=IwAR3h-A_YKLuqTdQdYf0UJRRvwUWYBb9oG4NeLKU3ZcJfN3UbF1kTQ5v3BH4

import java.util.HashSet;
import java.util.concurrent.Semaphore;


class Player extends Thread {

    static Semaphore sala = new Semaphore(12);
    static Semaphore soblekuvalna = new Semaphore(4);

    static int numPresobleceni = 0;
    static Semaphore numPresobleceniSemaphore = new Semaphore(1);

    static Semaphore cekajDrugi = new Semaphore(0);

    public void execute() throws InterruptedException {

        sala.acquire();
        System.out.println("Player inside.");

        soblekuvalna.acquire();
        System.out.println("Player in dressing room.");
        Thread.sleep(10);
        soblekuvalna.release();

        numPresobleceniSemaphore.acquire();
        numPresobleceni++;
        if(numPresobleceni == 12){
            // kazi mu na drugite oti site se gotovi
            cekajDrugi.release(12);
        }
        numPresobleceniSemaphore.release();

        // cekaj drugite da se presoblecat
        cekajDrugi.acquire();

        System.out.println("Game started.");
        Thread.sleep(100);
        System.out.println("Player done.");

        numPresobleceniSemaphore.acquire();
        numPresobleceni--;
        if(numPresobleceni == 0){
            System.out.println("Game finished.");
            // now that everyone has left the sala, other 12 players can enter
            sala.release(12);
        }
        numPresobleceniSemaphore.release();
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


public class VolleyballTournament {

    public static void main(String[] args) throws InterruptedException {
        HashSet<Thread> threads = new HashSet<Thread>();
        for (int i = 0; i < 60; i++) {
            Player ms = new Player();
            threads.add(ms);
        }

        // start them
        for(Thread t : threads)
            t.start();

        // wait for them at max 2000ms
        for(Thread t : threads)
            t.join(2000);

        // terminate if not finished
        boolean allFinished = true;
        for(Thread t : threads){
            if(t.isAlive()){
                allFinished = false;
                t.interrupt();
                System.out.println("Possible deadlock");
            }
        }

        if(allFinished)
            System.out.println("Tournament finished");

    }

}
