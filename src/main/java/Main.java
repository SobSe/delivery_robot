import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                String route = Main.generateRoute("RLRFR", 100);
                long freqR = 0;
                freqR = route.chars()
                        .filter((c) -> (c == 'R'))
                        .count();
                synchronized(Main.sizeToFreq) {
                    sizeToFreq.compute((int) freqR, (key, val) -> ((val == null) ? 1 : val + 1));
                }
            });
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {

            }
        }
        int keyMaxFreq = sizeToFreq
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .get()
                .getKey();
        int maxFreq = sizeToFreq.get(keyMaxFreq);
        System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n", keyMaxFreq, maxFreq);
        System.out.println("Другие размеры:");
        sizeToFreq.forEach((k, v) -> {if(k != keyMaxFreq) System.out.printf("- %d (%d раз)\n", k, v);});
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
