package go.builtin.channel;

import go.runtime.G;

import java.util.concurrent.atomic.AtomicBoolean;

public class SudoG {

    public volatile G g;
    public volatile Object[] elem;

    public volatile boolean isSelect;

    public volatile boolean success;

    public final AtomicBoolean selectDone = new AtomicBoolean();
    public volatile HChan c;
    public volatile SudoG waitlink = null;

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
        if (s.c != null) {
            throw new IllegalStateException("runtime: sudog with non-nil c");
        }
    }
}
