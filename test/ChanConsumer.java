import go.builtin.GoObject;
import go.builtin.channel.Channel;

public class ChanConsumer implements Runnable {
    private final Channel<? extends GoObject> chan;

    public ChanConsumer(Channel<? extends GoObject> chan) {
        this.chan = chan;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println(chan.receive());
        }
    }
}
