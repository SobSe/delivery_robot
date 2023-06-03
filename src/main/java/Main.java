import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final String LETTERS = "RLRFR";
    public static final int COUNT_THREADS = 1000;
    final static int ROUTE_LENGTH = 100;

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        Thread freqLog = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    printOfLeader();
                }
            }
        });
        freqLog.start();

        for (int i = 0; i < COUNT_THREADS; i++) {
            Thread thread = getThread();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {

            }
        }
        freqLog.interrupt();
    }

    private static Thread getThread() {
        Thread thread = new Thread(() -> {
            String route = generateRoute(LETTERS, ROUTE_LENGTH);
            long freqR = 0;
            freqR = route.chars()
                    .filter((c) -> (c == 'R'))
                    .count();
            synchronized(sizeToFreq) {
                sizeToFreq.compute((int) freqR, (key, val) -> ((val == null) ? 1 : val + 1));
                sizeToFreq.notify();
            }
        });
        return thread;
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void printOfLeader() {
        Map.Entry<Integer, Integer> maxFreq = sizeToFreq
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .get();
        System.out.printf("Текущее самое частое количество повторений %d (встретилось %d раз)\n",
                maxFreq.getKey(),
                maxFreq.getValue());
    }
}
