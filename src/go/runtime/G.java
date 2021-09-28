package go.runtime;

import go.builtin.channel.SudoG;

import java.util.WeakHashMap;

public class G {

    private static final WeakHashMap<Thread, G> weakGPool = new WeakHashMap<>();

    private G() {}

    public volatile Object param = null;
    public volatile boolean selectDone = false;
    public volatile SudoG waiting = null;
    public volatile Thread th = null;

    public static G getg() {
        Thread th = Thread.currentThread();
        if (weakGPool.containsKey(th)) {
            return weakGPool.get(th);
        }
        else {
            G g = new G();
            g.th = th;
            weakGPool.put(th, g);
            return g;
        }
    }
}
