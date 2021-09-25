package go.lang.channel;

import go.lang.tuple.Couple;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

class HChan {
    final int dataqsiz;
    final ArrayBlockingQueue<Object> buf;
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
    }

    public HChan() {
        this(0);
    }

    public boolean full() {
        if (dataqsiz == 0) {
            return recvq.isEmpty();
        }
        return buf.size() == dataqsiz;
    }

    public boolean empty() {
        if (dataqsiz == 0) {
            return sendq.isEmpty();
        }
        return buf.size() == 0;
    }

    // entrypoint for send
    static boolean startsend(HChan c, Object elem, boolean block) {
        if (c == null) {
            if (!block) {
                return false;
            }
            while (true) LockSupport.park();
        }
        return c.dosend(elem, block);
    }

    // send logic for non-null channels
    private boolean dosend(Object ep, boolean block) {

        // Fast path: check for failed non-blocking operation without acquiring the lock.
        if (!block && !closed && full()) {
            return false;
        }

        lock.lock(); // START CRITICAL SECTION
        if (closed) {
            lock.unlock(); // END CRITICAL SECTION
            throw new IllegalStateException("closed");
        }

        SudoG sg = recvq.pollFirst();
        if (sg != null) {
            // Found a waiting receiver. We pass the value we want to send
            // directly to the receiver, bypassing the channel buffer (if any).
            lock.unlock(); // END CRITICAL SECTION
            sg.elem = ep;
            sg.success = true;
            SudoG.unpark(sg);
            return true;
        }

        if (buf.size() < dataqsiz) {
            // Space is available in the channel buffer. Enqueue the element to send.
            buf.add(ep);
            lock.unlock(); // END CRITICAL SECTION
            return true;
        }

        if (!block) {
            lock.unlock(); // END CRITICAL SECTION
            return false;
        }

        // Block on the channel. Some receiver will complete our operation for us.
        SudoG mysg = SudoG.aquire();
        mysg.elem = ep;
        sendq.add(mysg);
        SudoG.park(mysg, lock); // END CRITICAL SECTION

        // someone woke us up.
        if(!mysg.success) {
            throw new IllegalStateException("send on closed channel");
        }
        return true;
    }

    // entrypoint for close
    public static void startclose(HChan c) {
        if (c == null) {
            throw new IllegalStateException("close of nil channel");
        }
        c.close();
    }

    // close logic for non-null channels
    private void close() {
        ArrayList<SudoG> sglist = new ArrayList<>();
        Predicate<SudoG> action = (SudoG sg) -> {
            sg.elem = null;
            sg.success = false;
            return sglist.add(sg);
        };

        lock.lock(); // BEGIN CRITICAL SECTION
        if (closed) {
            lock.unlock(); // END CRITICAL SECTION
            throw new IllegalStateException("close of closed channel");
        }
        closed = true;
        recvq.removeIf(action); // release all readers
        sendq.removeIf(action); // release all writers (they will panic)

        lock.unlock(); // END CRITICAL SECTION
        sglist.forEach(SudoG::unpark); // unpark all threads
    }

    // entrypoint for recv
    public static Couple<Boolean, Boolean> startrecv(HChan c, Object[] ep, boolean block) {
        if (c == null) {
            if (!block) {
                return Couple.of(false, false);
            }
            while (true) LockSupport.park();
        }
        return c.dorecv(ep, block);
    }

    // recv logic for non-null channels
    private Couple<Boolean, Boolean> dorecv(Object[] ep, boolean block) {

        // Fast path: check for failed non-blocking operation without acquiring the lock.
        if (!block && empty()) {
            if (!closed) {
                return Couple.of(false, false);
            }
            if (empty()) {
                return Couple.of(true, false);
            }
        }

        lock.lock(); // BEGIN CRITICAL SECTION

        if (closed && buf.isEmpty()) {
            lock.unlock(); // END CRITICAL SECTION
            return Couple.of(true, false);
        }

        SudoG sg = sendq.pollFirst();
        if (sg != null) {
            // Found a waiting sender. If buffer is size 0, receive value
            // directly from sender. Otherwise, receive from head of queue
            // and add sender's value to the tail of the queue.
            if (dataqsiz == 0) {
                ep[0] = sg.elem;
            } else {
                // Queue is full (sendq will only have senders in it
                // if they couldn't deposit more data into the buffer.)
                // Take the item at the head of the queue. Make the
                // sender enqueue its item at the tail of the queue.
                Object qp = buf.poll();
                ep[0] = qp;
                buf.offer(sg.elem);
            }
            sg.elem = null;
            lock.unlock(); // END CRITICAL SECTION
            sg.success = true;
            SudoG.unpark(sg);
            return Couple.of(true, true);
        }

        if (buf.size() > 0) {
            // Receive directly from queue
            Object qp = buf.poll();
            if (ep != null) {
                ep[0] = qp;
            }
            lock.unlock(); // END CRITICAL SECTION
            return Couple.of(true, true);
        }

        if (!block) {
            lock.unlock(); // END CRITICAL SECTION
            return Couple.of(false, false);
        }

        // no sender available: block on this channel.
        SudoG mysg = SudoG.aquire();
        recvq.add(mysg);
        SudoG.park(mysg, lock); // END CRITICAL SECTION

        // someone woke us up
        ep[0] = mysg.elem;
        return Couple.of(true, mysg.success);
    }
}
