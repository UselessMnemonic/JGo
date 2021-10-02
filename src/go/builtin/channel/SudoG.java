package go.builtin.channel;

import go.runtime.G;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SudoG {

    public volatile G g;
    public volatile Object[] elem;

    public volatile boolean isSelect;

    public volatile boolean success;

    public final AtomicReference<SudoG> waitlink = new AtomicReference<>(null);
    public volatile HChan c;

    private SudoG() {}

    public static SudoG aquireSudog() {
        return new SudoG();
    }

    public static void releaseSudog(SudoG s) {
        if (s.elem != null) {
            throw new IllegalStateException("runtime: sudog with non-nil elem");
        }
        if (s.isSelect) {
            throw new IllegalStateException("runtime: sudog with non-false isSelect");
        }
        if (s.waitlink.get() != null) {
            throw new IllegalStateException("runtime: sudog with non-nil waitlink");
        }
        if (s.c != null) {
            throw new IllegalStateException("runtime: sudog with non-nil c");
        }
        G gp = G.getg();
        if (gp.param != null) {
            throw new IllegalStateException("runtime: releaseSudog with non-nil gp.param");
        }
    }

    public String toString() {
        return "SudoG@%d".formatted(this.hashCode());
    }
}
