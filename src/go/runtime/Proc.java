package go.runtime;

import java.util.concurrent.locks.LockSupport;
import java.util.function.BiFunction;

public class Proc {

    public static void park(BiFunction<G, Object, Boolean> unlockf, Object lock) {
        final G gp = G.getg();
        if (unlockf == null && lock == null) {
            if (Proc.debugProc) System.out.printf("proc: indefinite park g=%s\n", gp);
            while (true) LockSupport.park();
        }
        if (unlockf != null && !unlockf.apply(gp, lock)) {
            if (Proc.debugProc) System.out.printf("proc: lock fail g=%s\n", gp);
            return;
        }
        if (Proc.debugProc) System.out.printf("proc: park g=%s\n", gp);
        while (gp.param == null) LockSupport.park();
        if (Proc.debugProc) System.out.printf("proc: unparked g=%s, param=%s\n", gp, gp.param);
    }

    public static void ready(G gp) {
        LockSupport.unpark(gp.th);
        if (Proc.debugProc) System.out.printf("proc: ready g=%s, param=%s\n", gp, gp.param);
    }

    public static final boolean debugProc = true;
}
