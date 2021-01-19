package threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;


/*
Едно племе јаде заеднична вечера од голем котел во кој има ограничен број порции. Секој член од племето сам се послужува,
доколку има храна во казанот. Доколку казанот е празен, се повикува готвачот за да зготви нов казан со храна. Притоа,
само еден член може да прави провекра на тоа дали има храна во казанот во одреден момент и околу казанот има место за
максимум тројца, што значи дека максимум тројца истовремено може да земаат храна, но бројот на членови кои јадат паралелно
не е ограничен.

Казанот е иницијално празен.

Вашата задача е да го синхронизирате претходното сценарио.
Во почетниот код кој е даден, дефинирана е класата TribeMember, која го симболизира однесувањето на членовите на племето.
Има повеќе инстанци од класата TribeMember кај кои методот execute() се повикува повеќе пати.

Во имплементацијата, можете да ги користите следните методи од веќе дефинираната променлива state:
state.isPotEmpty()
- Служи за проверка дали има храна во казанот. Овој метод манипулира со глобални променливи и треба да е во критичен регион.
- Доколку повеќе членови паралелно проверуваат, повикот ќе фрли исклучок.
state.fillPlate()
- Го симболизира земањето на храна од казанот.
- Доколку казанот е празен или повеќе од тројца паралелно земаат храна од казанот, повикот ќе фрли исклучок.
state.eat()
- Го симболизира конзумирањето на храната од страна на членовите на племето.
state.cook()
- Го симболизира готвењето на храна во казанот од страна на готвачот.
- Доколку казанот не е празен, овој повик ќе фрли исклучок.

Претходно назначените методи служат за проверка на точноста на сценариото и не смеат да бидат променети и мораат да бидат повикани.

Вашата задача е да ги имплементирате методите TribeMember.execute() и init(). При имплементацијата,
не смеете да додадете try-catch блок во нив.

Доколку имате грешка, ќе ја добиете пораката: "Procesot ne e sinhroniziran spored uslovite na zadacata"

По што ќе ви се прикаже логот на повикување на акциите и настанатите грешки.
Овој лог треба да ви послужи за увидување на тоа каде имате грешка во извршувањето на вашата задача.

Напомена: Поради конкурентниот пристап за логирањето, можно е некои од пораките да не се на позицијата каде што треба да се.
Токму затоа, овие пораки користете ги само како информација, но не се ослонувајте на нив.
 */


public class TribeDinner {

	static Semaphore plates;
	static Semaphore potEmptySemaphore;

	public static void init(int numBarbers) {
		plates = new Semaphore(3);
		potEmptySemaphore = new Semaphore(1);
	}

	static class TribeMember extends TemplateThread {

		public TribeMember(int numRuns) {
			super(numRuns);
		}

		public void execute() throws InterruptedException {

			potEmptySemaphore.acquire();
			if(state.isPotEmpty()){
				// drigite cekaat
				state.cook();
			}
			potEmptySemaphore.release();

			plates.acquire();
			state.fillPlate();
			plates.release();

			state.eat();

		}
	}

	// <editor-fold defaultstate="collapsed" desc="This is the template code" >
	static State state;

	static class State {

		private static final String _10_JADENJETO_NE_E_PARALELIZIRANO = "jadenjeto ne e paralelizirano. Site jadat eden po eden";
		private static final String _10_PRISTAPOT_DO_KAZANOT_NE_E_PARALELIZIRAN = "Pristapot do kazanot ne e paraleliziran. Treda da moze do trojca paralelno da zemaat hrana.";

		private static final String _10_DVAJCA_ISTOVREMENO_PROVERUVAAT = "Dvajca istovremeno proveruvaat dali kazanot e prazen. Maksimum eden e dozvoleno.";
		private static final String _7_NEMA_MESTO_POKRAJ_KAZANOT = "Nema mesto pokraj kazanot. Maksimum tri clena moze da zemaat hrana istovremeno.";
		private static final String _5_NE_MOZE_DA_JADE_OD_PRAZEN_KAZAN = "Ne moze da jade od prazen kazan. Treba da se povika 'state.cook()'";
		private static final String _5_NE_MOZE_DA_SE_GOTVI_VO_KAZAN_KOJ_NE_E_PRAZEN = "Ne moze da se gotvi vo kazan koj ne e prazen";

		private static final Random RANDOM = new Random();
		private static final int POT_CAPACITY = 15;

		private LimitedCounter platesLeft = new LimitedCounter(0, null, 0,
				null, 0, 5, _5_NE_MOZE_DA_JADE_OD_PRAZEN_KAZAN);

		private LimitedCounter checks = new LimitedCounter(0, 1, 10,
				_10_DVAJCA_ISTOVREMENO_PROVERUVAAT, null, 0, null);

		private LimitedCounter fills = new LimitedCounter(0, 3, 7,
				_7_NEMA_MESTO_POKRAJ_KAZANOT, null, 0, null);

		private LimitedCounter eat = new LimitedCounter(0);

		public State() {

		}

		public boolean isPotEmpty() throws RuntimeException {
			log(checks.incrementWithMax(), "proverka dali ima hrana vo kazanot");
			sleep(5);
			boolean res = platesLeft.getValue() == 0;
			log(checks.decrementWithMin(), null);
			return res;
		}

		public void fillPlate() throws RuntimeException {
			log(fills.incrementWithMax(), "zemanje na hrana");
			log(platesLeft.decrementWithMin(), null);
			sleep(5);
			log(platesLeft.incrementWithMax(), null);
			log(fills.decrementWithMin(), null);
		}

		public void eat() throws RuntimeException {
			log(eat.incrementWithMax(), "jadenje");
			sleep(10);
			log(eat.decrementWithMin(), null);
		}

		public void cook() throws RuntimeException {
			synchronized (State.class) {
				if (platesLeft.getValue() == 0) {
					platesLeft.setValue(POT_CAPACITY);
				} else {
					PointsException e = new PointsException(5,
							_5_NE_MOZE_DA_SE_GOTVI_VO_KAZAN_KOJ_NE_E_PRAZEN);
					log(e, null);
				}
			}
		}

		public void finalize() {
			if (fills.getMax() == 1) {
				logException(new PointsException(10,
						_10_PRISTAPOT_DO_KAZANOT_NE_E_PARALELIZIRAN));
			}
			if (eat.getMax() == 1) {
				logException(new PointsException(10,
						_10_JADENJETO_NE_E_PARALELIZIRANO));
			}
		}

		private List<String> actions = new ArrayList<String>();

		private void sleep(int range) {
			try {
				Thread.sleep(RANDOM.nextInt(range));
			} catch (InterruptedException e) {
			}
		}

		protected TemplateThread getThread() {
			TemplateThread t = (TemplateThread) Thread.currentThread();
			return t;
		}

		private synchronized void log(PointsException e, String action) {
			TemplateThread t = (TemplateThread) Thread.currentThread();
			if (e != null) {
				t.setException(e);
				actions.add(t.toString() + "\t(e): " + e.getMessage());
			} else if (action != null) {
				actions.add(t.toString() + "\t(a): " + action);
			}
		}

		private synchronized void logException(PointsException e) {
			actions.add("\t(e): " + e.getMessage());
		}

		public synchronized void printLog() {
			System.out
					.println("Poradi konkurentnosta za pristap za pecatenje, mozno e nekoja od porakite da ne e na soodvetnoto mesto.");
			System.out.println("Log na izvrsuvanje na akciite:");
			System.out.println("=========================");
			System.out.println("tip\tid\titer\takcija/error");
			System.out.println("=========================");
			for (String l : actions) {
				System.out.println(l);
			}
		}

		public void printStatus() {
			finalize();
			if (!TemplateThread.hasException) {
				int poeni = 25;
				if (PointsException.getTotalPoints() == 0) {
					System.out
							.println("Procesot e uspesno sinhroniziran. Osvoeni 25 poeni.");
				} else {
					poeni -= PointsException.getTotalPoints();
					PointsException.printErrors();
					System.out.println("Maksimalni osvoeni poeni: " + poeni);
				}

			} else {
				System.out
						.println("Procesot ne e sinhroniziran spored uslovite na zadacata");
				printLog();
				System.out
						.println("====================================================");
				PointsException.printErrors();
				int total = (25 - PointsException.getTotalPoints());
				if (total < 0) {
					total = 0;
				}
				System.out.println("Maksimum Poeni: " + total);
			}

		}
	}

	static class LimitedCounter {

		private int value;
		private Integer maxAllowed;
		private Integer minAllowed;
		private int maxErrorPoints;
		private int minErrorPoints;
		private String maxErrorMessage;
		private String minErrorMessage;

		private int max;

		public LimitedCounter(int value) {
			super();
			this.value = value;
			this.max = value;
		}

		public LimitedCounter(int value, Integer maxAllowed,
				int maxErrorPoints, String maxErrorMessage, Integer minAllowed,
				int minErrorPoints, String minErrorMessage) {
			super();
			this.value = value;
			this.max = value;
			this.maxAllowed = maxAllowed;
			this.minAllowed = minAllowed;
			this.maxErrorPoints = maxErrorPoints;
			this.minErrorPoints = minErrorPoints;
			this.maxErrorMessage = maxErrorMessage;
			this.minErrorMessage = minErrorMessage;
		}

		public int getMax() {
			return max;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public PointsException incrementWithMax() {
			synchronized (LimitedCounter.class) {
				value++;
				if (value > max) {
					max = value;
				}
				if (maxAllowed != null) {
					if (value > maxAllowed) {
						PointsException e = new PointsException(maxErrorPoints,
								maxErrorMessage);
						return e;
					}
				}
			}
			return null;
		}

		public PointsException decrementWithMin() {
			synchronized (LimitedCounter.class) {
				value--;
				if (minAllowed != null) {
					if (value < minAllowed) {
						PointsException e = new PointsException(minErrorPoints,
								minErrorMessage);
						return e;
					}
				}
			}
			return null;
		}
	}

	abstract static class TemplateThread extends Thread {

		static boolean hasException = false;
		int numRuns = 1;
		public int iteration = 0;
		protected Exception exception = null;

		public TemplateThread(int numRuns) {
			this.numRuns = numRuns;
		}

		abstract void execute() throws InterruptedException;

		@Override
		public void run() {
			try {
				for (int i = 0; i < numRuns && !hasException; i++) {
					execute();
					iteration++;

				}
			} catch (InterruptedException e) {
				// Do nothing
			} catch (Exception e) {
				exception = e;
				hasException = true;
			}
		}

		public void setException(Exception exception) {
			this.exception = exception;
			hasException = true;
		}

		@Override
		public String toString() {
			Thread current = Thread.currentThread();
			if (numRuns > 1) {
				return String.format("%s\t%d\t%d", ""
						+ current.getClass().getSimpleName().charAt(0),
						getId(), iteration);
			} else {
				return String
						.format("%s\t%d\t", ""
								+ current.getClass().getSimpleName().charAt(0),
								getId());
			}
		}
	}

	static class PointsException extends RuntimeException {

		private static HashMap<String, PointsException> exceptions = new HashMap<String, PointsException>();
		private int points;

		public PointsException(int points, String message) {
			super(message);
			this.points = points;
			exceptions.put(message, this);
		}

		public static int getTotalPoints() {
			int sum = 0;
			for (PointsException e : exceptions.values()) {
				sum += e.getPoints();
			}
			return sum;
		}

		public static void printErrors() {
			System.out.println("Gi imate slednite greski: ");
			for (Map.Entry<String, PointsException> e : exceptions.entrySet()) {
				System.out.println(String.format("[%s] : (-%d)", e.getKey(), e
						.getValue().getPoints()));
			}
		}

		public int getPoints() {
			return points;
		}
	}

	public static void main(String[] args) {
		try {
			start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void start() throws Exception {
		int brKonzumeri = 100;
		int numCustomerRuns = 3;
		init(brKonzumeri);

		state = new State();
		HashSet<Thread> threads = new HashSet<Thread>();

		for (int i = 0; i < brKonzumeri; i++) {
			TribeMember c = new TribeMember(numCustomerRuns);
			threads.add(c);
			c.start();
		}
		try {
			Thread.sleep(50);
		} catch (Exception e) {
			// do nothing
		}

		for (Thread t : threads) {
			t.join(1000);
		}

		for (Thread t : threads) {
			if (t.isAlive()) {
				t.interrupt();
				if (t instanceof TemplateThread) {
					TemplateThread tt = (TemplateThread) t;
					tt.setException(new PointsException(25, "DEADLOCK"));
				}
			}
		}
		state.printStatus();
	}
	// </editor-fold>

}

