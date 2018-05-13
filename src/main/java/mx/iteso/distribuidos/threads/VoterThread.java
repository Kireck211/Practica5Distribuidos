package mx.iteso.distribuidos.threads;

import mx.iteso.distribuidos.listeners.CoordinatorChangedListener;
import mx.iteso.distribuidos.listeners.PingAgainListener;

public class VoterThread {
    private static Thread thread = null;
    private static VoterExecution voter = null;

    public void initialize(CoordinatorChangedListener coordinatorChangedListener, PingAgainListener pingAgainListener) {
        voter = new VoterExecution(coordinatorChangedListener, pingAgainListener);
        thread = new Thread(voter);

        thread.start();
    }

    public void stop() throws InterruptedException {
        if (thread != null) {
            voter.terminate();
            thread.join();
        }
    }
}
