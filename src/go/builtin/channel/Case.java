package go.builtin.channel;

import go.builtin.GoObject;
import go.builtin.tuple.Couple;
import go.runtime.G;
import go.runtime.Proc;
import util.WaitingAssigner;
import util.WaitlinkAssigner;

import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.function.Consumer;

public final class Case {
    private final HChan c;
    private Object[] elem;
    private final boolean isSend;

    public <T extends GoObject> Case(Channel<T> chan) {
        this.c = chan.hchan;
        this.elem = new Object[] {null};
        this.isSend = false;
    }

    public <T extends GoObject> Case(Channel<T> chan, T elem) {
        this.c = chan.hchan;
        this.elem = new Object[] {elem};
        this.isSend = true;
    }

    public <T extends GoObject> T value() {
        return (T) elem[0];
    }

    public static Couple<Integer, Boolean> select(Case[] scases, boolean block) {
        int[] pollorder = new int[scases.length];
        int[] lockorder = new int[scases.length];

        // generate permuted order
        SplittableRandom fastrandn = new SplittableRandom();
        int norder = 0;
        for (int i = 0; i < scases.length; i++) {
            Case cas = scases[i];

            // Omit cases without channels from the poll and lock orders.
            if (cas.c == null) {
                cas.elem = null;
                continue;
            }

            int j = fastrandn.nextInt(norder + 1);
            pollorder[norder] = pollorder[j];
            pollorder[j] = (short) i; // why not lol
            norder++;
        }
        pollorder = Arrays.copyOf(pollorder, norder);
        lockorder = Arrays.copyOf(lockorder, norder);

        // sort the cases by Hchan address (hash) to get the locking order.
        for (int i = 0; i < lockorder.length; i++) {
            int j = i;
            HChan c = scases[pollorder[i]].c;
            while (j > 0 && scases[lockorder[(j-1)/2]].c.hashCode() < c.hashCode()) {
                int k = (j - 1) / 2;
                lockorder[j] = lockorder[k];
                j = k;
            }
            lockorder[j] = lockorder[i];
        }
        for (int i = lockorder.length - 1; i >= 0; i--) {
            int o = lockorder[i];
            HChan c = scases[o].c;
            lockorder[i] = lockorder[0];
            int j = 0;
            while (true) {
                int k = (j * 2) + 1;
                if (k >= i) {
                    break;
                }
                if ((k+1 < i) && scases[lockorder[k]].c.hashCode() < scases[lockorder[k+1]].c.hashCode()) {
                    k++;
                }
                if (c.hashCode() < scases[lockorder[k]].c.hashCode()) {
                    lockorder[j] = lockorder[k];
                    j = k;
                    continue;
                }
                break;
            }
            lockorder[j] = o;
        }

        if (Case.debugSelect) {
            for (int i = 0; i+1 < lockorder.length; i++) {
                if (scases[lockorder[i]].c.hashCode() > scases[lockorder[i+1]].c.hashCode()) {
                    System.out.printf("i=%d, x=%s, y=%s", i, lockorder[i], lockorder[i+1]);
                    throw new IllegalStateException("select: broken sort");
                }
            }
        }

        // lock all the channels involved in the select
        sellock(scases, lockorder);

        G gp;
        SudoG sg;
        HChan c;
        Case k;
        SudoG sglist;
        SudoG sgnext;
        Consumer<SudoG> nextp;

        // pass 1 - look for something already waiting
        int casi;
        Case cas;
        boolean caseSuccess;
        boolean recvOK = false;
        for (int casei = 0; casei < pollorder.length; casei++) {
            casi = casei;
            cas = scases[casi];
            c = cas.c;
            if (cas.isSend) {
                if (c.closed) return sclose(scases, lockorder);
                sg = c.recvq.dequeue();
                if (sg != null) return send(c, sg, cas, scases, lockorder, casi, recvOK);
                if (c.buf.size() < c.dataqsiz) return bufsend(c, cas, scases, lockorder, casi, recvOK);
            } else {
                sg = c.sendq.dequeue();
                if (sg != null) return recv(c, sg, cas, scases, lockorder, casi);
                if (c.buf.size() > 0) return bufrecv(c, cas, scases, lockorder, casi);
                if (c.closed) return rclose(scases, lockorder, cas, casi);
            }
        }

        if (!block) {
            selunlock(scases, lockorder);
            casi = -1;
            return retc(casi, recvOK);
        }

        // pass 2 - enqueue on all chans
        gp = G.getg();
        if (gp.waiting != null) {
            throw new IllegalStateException("gp.waiting != null");
        }
        nextp = new WaitingAssigner(gp);
        for (int casei = 0; casei < lockorder.length; casei++) {
            casi = casei;
            cas = scases[casi];
            c = cas.c;
            sg = SudoG.aquireSudog();
            sg.g = gp;
            sg.isSelect = true;
            sg.elem = cas.elem;
            sg.c = c;
            nextp.accept(sg);
            nextp = new WaitlinkAssigner(sg);

            if (cas.isSend) c.sendq.enqueue(sg);
            else c.recvq.enqueue(sg);
        }

        // wait for someone to wake us up
        gp.param = null;
        Proc.park(Case::selparkcommit, null);

        sellock(scases, lockorder);

        gp.selectDone = false;
        sg = (SudoG) gp.param;
        gp.param = null;

        // pass 3 - dequeue from unsuccessful chans
        // otherwise they stack up on quiet channels
        // record the successful case, if any.
        // We singly-linked up the SudoGs in lock order.
        casi = -1;
        cas = null;
        caseSuccess = false;
        sglist = gp.waiting;
        // Clear all elem before unlinking from gp.waiting.
        for (SudoG sg1 = gp.waiting; sg1 != null; sg1 = sg1.waitlink) {
            sg1.isSelect = false;
            sg1.elem = null;
            sg1.c = null;
        }
        gp.waiting = null;

        for (int casei = 0; casei < lockorder.length; casei++) {
            k = scases[casei];
            if (sg == sglist) {
                // sg has already been dequeued by the G that woke us up.
                casi = casei;
                cas = k;
                caseSuccess = sglist.success;
            } else {
                c = k.c;
                if (k.isSend) c.sendq.remove(sglist);
                else c.recvq.remove(sglist);
            }
            sgnext = sglist.waitlink;
            sglist.waitlink = null;
            SudoG.releaseSudog(sglist);
            sglist = sgnext;
        }

        if (cas == null) throw new IllegalStateException("selectgo: bad wakeup");

        if (cas.isSend) {
            if (!caseSuccess) return sclose(scases, lockorder);
        } else {
            recvOK = caseSuccess;
        }

        selunlock(scases, lockorder);
        return retc(casi, recvOK);
    }

    private static Couple<Integer, Boolean> bufrecv(HChan c, Case cas, Case[] scases, int[] lockorder, int casi) {
        Object[] qp = c.buf.remove();
        if (cas.elem != null) {
            cas.elem[0] = qp[0];
        }
        selunlock(scases, lockorder);
        return retc(casi, true);
    }

    private static Couple<Integer, Boolean> bufsend(HChan c, Case cas, Case[] scases, int[] lockorder, int casi, boolean recvOK) {
        c.buf.add(cas.elem);
        selunlock(scases, lockorder);
        return retc(casi,recvOK);
    }

    private static Couple<Integer, Boolean> recv(HChan c, SudoG sg, Case cas, Case[] scases, int[] lockorder, int casi) {
        c.recv(sg, cas.elem, () -> selunlock(scases, lockorder));
        return retc(casi, true);
    }

    private static Couple<Integer, Boolean> rclose(Case[] scases, int[] lockorder, Case cas, int casi) {
        selunlock(scases, lockorder);
        if (cas.elem != null) {
            cas.elem[0] = null;
        }
        return retc(casi, false);
    }

    private static Couple<Integer, Boolean> send(HChan c, SudoG sg, Case cas, Case[] scases, int[] lockorder, int casi, boolean recvOK) {
        c.send(sg, cas.elem, () -> selunlock(scases, lockorder));
        return retc(casi, recvOK);
    }

    private static Couple<Integer, Boolean> retc(int casi, boolean recvOK) {
        return Couple.of(casi, recvOK);
    }

    private static Couple<Integer, Boolean> sclose(Case[] scases, int[] lockorder) {
        selunlock(scases, lockorder);
        throw new IllegalStateException("send on closed channel"); // TODO panic
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
            if (i > 0 && c == cases[lockorder[i-1]].c) {
                continue; // will unlock it on the next iteration
            }
            c.lock.unlock();
        }
    }

    private static boolean selparkcommit(G gp, Object ignore) {
        HChan lastc = null;
        for (SudoG sg = gp.waiting; sg != null; sg = sg.waitlink) {
            if (sg.c != lastc && lastc != null) {
                // As soon as we unlock the channel, fields in
                // any sudog with that channel may change,
                // including c and waitlink. Since multiple
                // sudogs may have the same channel, we unlock
                // only after we've passed the last instance
                // of a channel.
                lastc.lock.unlock();
            }
            lastc = sg.c;
        }
        if (lastc != null) {
            lastc.lock.unlock();
        }
        return true;
    }

    public static final boolean debugSelect = true;
}
