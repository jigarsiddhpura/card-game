public class VolatileExample {
    // Example 1: Basic volatile usage
    private static volatile boolean flag = false;

    // Example 2: Demonstrating the need for volatile
    private static class Counter {
        private volatile int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Example 1: Using volatile for thread communication
        Thread writerThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            flag = true;
            System.out.println("Flag set to true");
        });

        Thread readerThread = new Thread(() -> {
            while (!flag) {
                // Busy-wait until flag becomes true
            }
            System.out.println("Flag is now true, exiting reader thread");
        });

        writerThread.start();
        readerThread.start();

        writerThread.join();
        readerThread.join();

        // Example 2: Demonstrating volatile for visibility
        Counter counter = new Counter();

        Thread incrementThread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        });

        Thread readThread = new Thread(() -> {
            while (counter.getCount() < 1000) {
                // Busy-wait until count reaches 1000
            }
            System.out.println("Count has reached 1000");
        });

        incrementThread.start();
        readThread.start();

        incrementThread.join();
        readThread.join();
    }
}