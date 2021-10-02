package go.runtime;

import go.builtin.channel.SudoG;

import java.util.LinkedList;

public class WaitQ extends LinkedList<SudoG> {
    public SudoG dequeue() {
        for (;;) {
            SudoG sgp = pollFirst();
            if (sgp == null) {
                return null;
            }
            if (sgp.isSelect && !sgp.g.selectDone.compareAndSet(false, true)) {
                continue;
            }
            return sgp;
        }
    }

    public void enqueue(SudoG sgp) {
        this.addLast(sgp);
    }
}
