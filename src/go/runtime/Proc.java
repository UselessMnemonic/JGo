package go.runtime;

import java.util.concurrent.locks.LockSupport;
import java.util.function.BiFunction;

public class Proc {

    public static void park(BiFunction<G, Object, Boolean> unlockf, Object lock) {
        final G gp = G.getg();
        G.Status status = gp.status.get();
        if (status != G.Status.RUNNING) {
            throw new IllegalStateException("gopark: bad g status");
        }

        gp.casgstatus(G.Status.RUNNING, G.Status.WAITING);

        if (unlockf != null) {
            if (!unlockf.apply(gp, lock)) {
                gp.casgstatus(G.Status.WAITING, G.Status.RUNNABLE);
            }
        }

        while (!gp.status.compareAndSet(G.Status.RUNNABLE, G.Status.RUNNING)) {
            LockSupport.park();
        }

        /*synchronized (gp.parklock) {
            gp.parklock[0] = unlockf.apply(gp, lock);
            if (Proc.debugProc) System.out.printf("proc: park g=%s parklock=%s\n", gp, gp.parklock[0]);

            while (gp.parklock[0]) try {
                gp.parklock.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Proc.debugProc) System.out.printf("proc: unparked g=%s, param=%s\n", gp, gp.param);
        }*/
    }

    public static void ready(G gp) {
        G.Status status = gp.status.get();
        if (status != G.Status.WAITING) {
            throw new IllegalArgumentException("bad g->status in ready");
        }
        gp.casgstatus(G.Status.WAITING, G.Status.RUNNABLE);
        LockSupport.unpark(gp.th);
    }

    public static final boolean debugProc = false;
}
