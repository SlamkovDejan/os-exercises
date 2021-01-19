package threads;

class MyThread extends Thread{

    @Override
    public void run() {
        System.out.println("Hello from MyThread");
    }

}

class MyRunnableClass implements Runnable{

    @Override
    public void run() {
        System.out.println("Hello from MyRunnableClass");
    }

}


public class Main {

    public static void main(String[] args) {

        MyThread myThread = new MyThread();
        MyRunnableClass myRunnableClass = new MyRunnableClass();

        myThread.start();
        // myThread.join();

        if(myThread.isAlive())
            myThread.interrupt();

        Thread wrapper = new Thread(myRunnableClass);
        wrapper.start();

    }

}
