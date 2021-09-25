package go.lang.channel;

import java.util.concurrent.locks.Lock;

class SudoG {
    public Object elem;
    public boolean success;

    private SudoG() {}

    public static SudoG aquire() {
        // TODO
    }

    public static void unpark(SudoG sg) {
        // TODO
    }

    public static void park(SudoG sg, Lock lock) {
        // TODO
    }
}
