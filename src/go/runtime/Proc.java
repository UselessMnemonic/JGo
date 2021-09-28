package go.runtime;

import java.util.concurrent.locks.LockSupport;
import java.util.function.BiFunction;

public class Proc {

    public static void park(BiFunction<G, Object, Boolean> unlockf, Object lock) {
        if (lock == null) {
            while (true) LockSupport.park();
        } else {
            final G gp = G.getg();
            final boolean[] b = new boolean[] {false};
            LockSupport.setCurrentBlocker(lock);
            if (unlockf != null && unlockf.apply(gp, lock)) {
                LockSupport.setCurrentBlocker(null);
                return;
            }
            while (!b[0]) LockSupport.park(b);
        }
    }

    public static void ready(G gp) {
        if (LockSupport.getBlocker(gp.th) instanceof boolean[] b) {
            b[0] = true;
        }
        LockSupport.unpark(gp.th);
    }
}
