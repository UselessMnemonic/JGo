package go.builtin.channel;

import go.builtin.tuple.Couple;
import go.runtime.G;
import go.runtime.Proc;
import go.runtime.WaitQ;
import util.Action;
import util.EmptyQueue;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class HChan {
    final int dataqsiz;
    final Queue<Object[]> buf;
    boolean closed;
    final WaitQ recvq;
    final WaitQ sendq;
    final ReentrantLock lock;

    public HChan(int size) {
        dataqsiz = size;
        buf = new ArrayBlockingQueue<>(dataqsiz);
        recvq = new WaitQ();
        sendq = new WaitQ();
        lock = new ReentrantLock();
        closed = false;
    }

    public HChan() {
        dataqsiz = 0;
        buf = new EmptyQueue<>();
        recvq = new WaitQ();
        sendq = new WaitQ();
        lock = new ReentrantLock();
        closed = false;
    }

    private boolean full() {
        if (dataqsiz == 0) {
            return recvq.isEmpty();
        }
        return buf.size() == dataqsiz;
    }

    // entrypoint for send
    public static boolean chansend(HChan c, Object elem, boolean block) {
        if (HChan.debugChan)  System.out.printf("chansend: chan=%s\n", c);

        if (c == null) {
            if (!block) {
                return false;
            }
            Proc.park(null,null);
            throw new IllegalStateException("unreachable");
        }
        return c.chansend(new Object[] {elem}, block);
    }

    // send logic for non-null channels
    boolean chansend(Object[] ep, boolean block) {

        // Fast path: check for failed non-blocking operation without acquiring the lock.
        if (!block && !closed && full()) {
            return false;
        }

        lock.lock(); // START CRITICAL SECTION

        if (closed) {
            lock.unlock(); // END CRITICAL SECTION
            throw new IllegalStateException("closed");
        }

        SudoG sg = recvq.dequeue();
        if (sg != null) {
            // Found a waiting receiver. We pass the value we want to send
            // directly to the receiver, bypassing the channel buffer (if any)
            send(sg, ep, lock::unlock); // END CRITICAL SECTION
            return true;
        }

        if (buf.size() < dataqsiz) {
            // Space is available in the channel buffer. Enqueue the element to send.
            if (HChan.debugChan)  System.out.printf("chansend: buf chan=%s, ep=%s\n", this, ep[0]);
            buf.add(ep);
            lock.unlock(); // END CRITICAL SECTION
            return true;
        }

        if (!block) {
            lock.unlock(); // END CRITICAL SECTION
            return false;
        }

        // Block on the channel. Some receiver will complete our operation for us.
        G gp = G.getg();
        SudoG mysg = SudoG.aquireSudog();
        mysg.elem = ep;
        mysg.waitlink = null;
        mysg.g = gp;
        mysg.isSelect = false;
        mysg.c = this;
        gp.waiting = mysg;
        gp.param = null;
        if (HChan.debugChan)  System.out.printf("chansend: park chan=%s, mysg=%s, gp=%s\n", this, mysg, gp);
        sendq.enqueue(mysg);
        Proc.park(HChan::charnparkcommit, lock); // END CRITICAL SECTION

        // someone woke us up.
        if (HChan.debugChan)  System.out.printf("chansend: wake chan=%s, mysg.elem=%s, gp=%s\n", this, mysg.elem, gp);
        if (mysg != gp.waiting) {
            throw new IllegalStateException("G waiting list is corrupted");
        }
        gp.waiting = null;
        boolean closed = !mysg.success;
        gp.param = null;
        mysg.c = null;
        SudoG.releaseSudog(mysg);
        if(closed) {
            if (!this.closed) {
                throw new IllegalStateException("chansend: spurious wakeup");
            }
            throw new IllegalStateException("send on closed channel"); // TODO panic
        }
        return true;
    }

    void send(SudoG sg, Object[] src, Action unlockf) {
        if (HChan.debugChan)  System.out.printf("chansend: send chan=%s, sg=%s, src=%s\n", this, sg, src);

        if (sg.elem != null) {
            sg.elem[0] = src[0];
            sg.elem = null;
        }
        G gp = sg.g;
        unlockf.invoke();
        gp.param = sg;
        sg.success = true;
        Proc.ready(gp);
    }

    // entrypoint for close
    public static void closechan(HChan c) {
        if (c == null) {
            throw new IllegalStateException("close of nil channel"); // TODO panic
        }
        c.closechan();
    }

    // close logic for non-null channels
    void closechan() {
        lock.lock(); // BEGIN CRITICAL SECTION
        if (closed) {
            lock.unlock(); // END CRITICAL SECTION
            throw new IllegalStateException("close of closed channel"); // TODO panic
        }

        closed = true;

        ArrayList<G> glist = new ArrayList<>();

        // release all readers
        for (;;) {
            SudoG sg = recvq.dequeue();
            if (sg == null) {
                break;
            }
            if (sg.elem != null) {
                sg.elem[0] = null;
                sg.elem = null;
            }
            G gp = sg.g;
            gp.param = sg;
            sg.success = false;
            glist.add(gp);
        }

        // release all writers (they will panic)
        for (;;) {
            SudoG sg = sendq.dequeue();
            if (sg == null) {
                break;
            }
            sg.elem = null;
            G gp = sg.g;
            gp.param = sg;
            sg.success = false;
            glist.add(gp);
        }
        lock.unlock(); // END CRITICAL SECTION

        glist.forEach(Proc::ready); // unpark all threads
    }

    private boolean empty() {
        if (dataqsiz == 0) {
            return sendq.isEmpty();
        }
        return buf.size() == 0;
    }

    // entrypoint for recv
    public static Couple<Boolean, Boolean> chanrecv(HChan c, Object[] ep, boolean block) {
        if (HChan.debugChan)  System.out.printf("chanrecv: chan=%s\n", c);

        if (c == null) {
            if (!block) {
                return Couple.of(false, false);
            }
            Proc.park(null, null);
            throw new IllegalStateException("unreachable");
        }
        return c.chanrecv(ep, block);
    }

    // recv logic for non-null channels
    Couple<Boolean, Boolean> chanrecv(Object[] ep, boolean block) {

        // Fast path: check for failed non-blocking operation without acquiring the lock.
        if (!block && empty()) {
            if (!closed) {
                return Couple.of(false, false);
            }
            if (empty()) {
                if (ep != null) {
                    ep[0] = null;
                }
                return Couple.of(true, false);
            }
        }

        lock.lock(); // BEGIN CRITICAL SECTION

        if (closed && buf.isEmpty()) {
            lock.unlock(); // END CRITICAL SECTION
            if (ep != null) {
                ep[0] = null;
            }
            return Couple.of(true, false);
        }

        SudoG sg = sendq.dequeue();
        if (sg != null) { // recv
            // Found a waiting sender. If buffer is size 0, receive value
            // directly from sender. Otherwise, receive from head of queue
            // and add sender's value to the tail of the queue.
            recv(sg, ep, lock::unlock); // END CRITICAL SECTION
            return Couple.of(true, true);
        }

        if (buf.size() > 0) {
            // Receive directly from queue
            Object[] qp = buf.remove();
            if (ep != null) {
                ep[0] = qp[0];
            }
            lock.unlock(); // END CRITICAL SECTION
            return Couple.of(true, true);
        }

        if (!block) {
            lock.unlock(); // END CRITICAL SECTION
            return Couple.of(false, false);
        }

        // no sender available: block on this channel.
        G gp = G.getg();
        SudoG mysg = SudoG.aquireSudog();
        mysg.elem = ep;
        mysg.waitlink = null;
        gp.waiting = mysg;
        mysg.g = gp;
        mysg.isSelect = false;
        mysg.c = this;
        gp.param = null;
        if (HChan.debugChan)  System.out.printf("chanrecv: park chan=%s, mysg=%s, gp=%s\n", this, mysg, gp);
        recvq.enqueue(mysg);
        Proc.park(HChan::charnparkcommit, lock); // END CRITICAL SECTION

        // someone woke us up
        if (HChan.debugChan)  System.out.printf("chanrecv: wake chan=%s, mysg.elem=%s, gp=%s\n", this, mysg.elem, gp);
        if (mysg != gp.waiting) {
            throw new IllegalStateException("G waiting list is corrupted");
        }
        gp.waiting = null;
        boolean success = mysg.success;
        gp.param = null;
        mysg.c = null;
        SudoG.releaseSudog(mysg);
        return Couple.of(true, success);
    }

    void recv(SudoG sg, Object[] ep, Action unlockf) {
        if (HChan.debugChan)  System.out.printf("chanrecv: recv chan=%s, sg=%s, ep=%s\n", this, sg, ep);
        if (dataqsiz == 0) {
            if (ep != null) {
                ep[0] = sg.elem[0];
            }
        } else {
            // Queue is full (sendq will only have senders in it
            // if they couldn't deposit more data into the buffer.)
            // Take the item at the head of the queue. Make the
            // sender enqueue its item at the tail of the queue.
            Object[] qp = buf.remove();
            if (ep != null) {
                ep[0] = qp[0];
            }
            buf.add(sg.elem);
        }
        sg.elem = null;
        G gp = sg.g;
        unlockf.invoke();
        gp.param = sg;
        sg.success = true;
        Proc.ready(gp);
    }

    private static boolean charnparkcommit(G gp, Object chanLock) {
        ((Lock) chanLock).unlock();
        return true;
    }

    private static final boolean debugChan = true;

    @Override
    public int hashCode() {
        return Objects.hash(lock, sendq, recvq);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        else if (o instanceof HChan other) {
            return (this.lock == other.lock)
                && (this.sendq == other.sendq)
                && (this.recvq == other.recvq);
        }
        else return false;
    }
}
