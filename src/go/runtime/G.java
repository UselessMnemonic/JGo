package go.runtime;

import go.builtin.channel.SudoG;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class G {

    private static final WeakHashMap<Thread, G> weakGPool = new WeakHashMap<>();

    private G(Thread m) {
        this.th = m;
    }

    public volatile Object param = null;
    public final AtomicBoolean selectDone = new AtomicBoolean();
    public final AtomicReference<SudoG> waiting = new AtomicReference<>(null);
    public final Thread th;
    public final AtomicReference<Status> status = new AtomicReference<>(Status.RUNNING);

    public static G getg() {
        Thread th = Thread.currentThread();
        if (weakGPool.containsKey(th)) {
            return weakGPool.get(th);
        }
        else {
            G g = new G(th);
            weakGPool.put(th, g);
            return g;
        }
    }

    public void casgstatus(Status oldval, Status newval) {
        if (oldval == newval) {
            throw new IllegalArgumentException("casgstatus: bad incoming values");
        }
        while (!status.compareAndSet(oldval, newval)) {
            if (oldval == Status.WAITING && status.get() == Status.RUNNABLE) {
                throw new IllegalStateException("casgstatus: waiting for Gwaiting but is Grunnable");
            }
            //LockSupport.parkNanos(1000);
        }
    }

    public String toString() {
        return "G@%d".formatted(this.hashCode());
    }

    public enum Status {
        WAITING,
        RUNNABLE,
        RUNNING
    }
}
