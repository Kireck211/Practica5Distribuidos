package mx.iteso.distribuidos.threads;

import mx.iteso.distribuidos.listeners.CoordinatorDownListener;
import mx.iteso.distribuidos.listeners.VoteTriggerListener;

public class PingThread {
    private static Thread thread = null;
    private static PingExecution ping = null;

    public void initialize(VoteTriggerListener voteTriggerListener) {
        ping = new PingExecution(voteTriggerListener);
        thread = new Thread(ping);

        thread.start();
    }

    public void stop() throws InterruptedException {
        if (thread != null) {
            ping.terminate();
            thread.join();
        }
    }

    public void enablePing(String coordinator) {
        if (thread != null) {
            ping.enablePing(coordinator);
        }
    }

    public void disablePing() {
        if (thread != null) {
            ping.disablePing();
        }
    }
}
