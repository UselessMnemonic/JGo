package go.runtime;

import go.builtin.channel.SudoG;

import java.util.WeakHashMap;

public class G {

    private static final WeakHashMap<Thread, G> weakGPool = new WeakHashMap<>();

    private G(Thread m) {
        this.th = m;
    }

    public volatile Object param = null;
    public volatile boolean selectDone = false;
    public volatile SudoG waiting = null;
    public final Thread th;

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
}
