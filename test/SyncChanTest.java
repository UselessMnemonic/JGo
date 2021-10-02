import go.builtin.channel.Channel;
import go.builtin.Int8;

public class SyncChanTest {

    public static void main(String[] args) {

        Channel<Int8> ch = Channel.make(Int8.goClass);
        RandomInt8ChanSender sender = new RandomInt8ChanSender(ch);
        ChanConsumer r1 = new ChanConsumer(ch);
        ChanConsumer r2 = new ChanConsumer(ch);

        new Thread(sender).start();
        new Thread(r1).start();

        r2.run();
    }
}
