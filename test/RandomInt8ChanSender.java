import go.builtin.Int8;
import go.builtin.channel.Channel;

import java.util.Random;

class RandomInt8ChanSender implements Runnable {
    private final Channel<Int8> ch;

    public RandomInt8ChanSender(Channel<Int8> ch) {
        this.ch = ch;
    }

    @Override
    public void run() {
        Random random = new Random();
        Int8 byt = new Int8();
        while (true) {
            byt.assign((byte) random.nextInt());
            ch.send(byt.clone());
        }
    }
}
