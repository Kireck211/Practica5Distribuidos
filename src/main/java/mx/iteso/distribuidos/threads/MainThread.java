package mx.iteso.distribuidos.threads;

public class MainThread {
    private static Thread thread = null;
    private static NormalExecution normal = null;

    public void initialize() {
        normal = new NormalExecution();
        thread = new Thread(normal);

        thread.start();
    }

    public void stop() throws InterruptedException {
        if (thread != null) {
            normal.terminate();
            thread.join();
        }
    }
}
