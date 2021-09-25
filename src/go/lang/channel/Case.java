package go.lang.channel;

import go.lang.GoObject;
import go.lang.tuple.Couple;

public abstract class Case {
    final HChan c;
    final GoObject sendElement;

    public <T extends GoObject> Case(Channel<T> chan) {
        this.c = chan.hchan;
        this.sendElement = null;
    }
    public <T extends GoObject> Case(Channel<T> chan, T elem) {
        this.c = chan.hchan;
        this.sendElement = elem;
    }

    static void select(Case... cases) {
        // TODO
    }

    private static Couple<Integer, Boolean> select(Case[] cas0, int[] order, int nsends, int nrecvs, boolean block) {
        // TODO
    }
}
