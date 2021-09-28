package go.builtin.channel;

import go.builtin.GoObject;
import go.builtin.tuple.Couple;
import go.runtime.G;

public final class Case {/*
    private HChan c;
    private Object[] elem;

    public <T extends GoObject> Case(Channel<T> chan) {
        this.c = chan.hchan;
        this.elem = null;
    }

    public <T extends GoObject> Case(Channel<T> chan, T elem) {
        this.c = chan.hchan;
        this.elem = new Object[] {elem};
    }

    static Case select(Case... cases) {
        // TODO
    }

    private static Couple<Integer, Boolean> select(Case[] scases, int[] order, int nsends, int nrecvs, boolean block) {
    }

    private static void sellock(Case[] cases, int[] lockorder) {
        HChan c = null;
        for (int o : lockorder) {
            HChan c0 = cases[o].c;
            if (c0 != c) {
                c = c0;
                c.lock.lock();
            }
        }
    }

    private static void selunlock(Case[] cases, int[] lockorder) {
        for (int i = lockorder.length - 1; i >= 0; i--) {
            HChan c = cases[lockorder[i]].c;
            if (i > 0 && c == cases[lockorder[i - 1]].c) {
                continue; // will unlock it on the next iteration
            }
            c.lock.unlock();
        }
    }

    private static boolean selparkcommit(G gp, Object ignore) {
        HChan lastc = null;
        for (SudoG sg = gp.waiting; sg != null; sg = sg.waitlink) {
            if (sg.c != lastc && lastc != null) {
                lastc.lock.unlock();
            }
            lastc = sg.c;
        }
        if (lastc != null) {
            lastc.lock.unlock();
        }
        return true;
    }*/
}
