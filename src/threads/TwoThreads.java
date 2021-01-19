package threads;

/*
Извршете го примерот од TwoThreads.java. Потоа, модифицирајте ја програмата така што ќе користите само една класа за нитки,
ThreadAB. Во конструкторот на класата ќе се предадат двата стринга кои соодветната инстанца треба да ги отпечати.
Нитката не треба да ја наследува класата Thread. Однесувањето на новата програма треба да биде исто како на оригиналната,
односно повторно треба да имате две нитки кои ќе го извршуваат посебно методот run(): едната нитка ќе печати A и B,
додека другата ќе печати 1 и 2.

Што би се случило доколку едната нитка треба да ја испечати целата азбука, а другата нитка броевите од 1 до 26?
Дали можете да го предвидите изгледот на излезот од програмата?
 */

class Thread1 extends Thread {
    public void run() {
        System.out.println("A");
        System.out.println("B");
    }
}

class Thread2 extends Thread {
    public void run() {
        System.out.println("1");
        System.out.println("2");
    }
}

class ThreadAB implements Runnable{

    private String one, two;

    public ThreadAB(String one, String two) {
        this.one = one;
        this.two = two;
    }

    @Override
    public void run() {
        System.out.println(one);
        System.out.println(two);
    }

}

public class TwoThreads {

    public static void main(String[] args) {
        // wrap ThreadAB into Thread because it does not extend Thread
        new Thread(new ThreadAB("A", "B")).start();
        new Thread(new ThreadAB("1", "2")).start();
//        new Thread1().start();
//        new Thread2().start();
    }

}