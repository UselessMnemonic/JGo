import go.builtin.Int8;
import go.builtin.channel.Case;
import go.builtin.channel.Channel;
import go.builtin.tuple.Couple;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class SelectTest {

    public static volatile boolean run = true;

    public static void main(String[] args) {
        Channel<Int8> ch1 = Channel.make(Int8.goClass);
        Channel<Int8> ch2 = Channel.make(Int8.goClass);

        new Thread(new RandomInt8ChanSender(ch1)).start();
        new Thread(new RandomInt8ChanSender(ch2)).start();

        while (run) {
            Case[] cases0 = new Case[] {
                    new Case(ch1),
                    new Case(ch2)
            };
            Couple<Integer, Boolean> result0 = Case.select(cases0, true);
            if (result0.a == 0) {
                Case winner0 = cases0[result0.a];
                System.out.println("Case 1 won: " + winner0.value());
            }
            else if (result0.a == 1) {
                Case winner0 = cases0[result0.a];
                System.out.println("Case 2 won: " + winner0.value());
            }
            else {
                System.out.println("Default case won!");
            }
        }
    }
}

class RandomInt8ChanSender implements Runnable {
    private final Channel<Int8> ch;

    public RandomInt8ChanSender(Channel<Int8> ch) {
        this.ch = ch;
    }

    @Override
    public void run() {
        Random random = new Random();
        Int8 byt = new Int8((byte) 0);
        try {
            while (SelectTest.run) {
                byt.assign((byte) random.nextInt());
                ch.send(byt.clone());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}