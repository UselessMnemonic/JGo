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
            Case[] cases = new Case[] {
                    new Case(ch1),
                    new Case(ch2)
            };
            Couple<Integer, Boolean> result = Case.select(cases, true);
            Case winner;

            if (result.a == 0) {
                winner = cases[0];
                System.out.println("Case 1 won: " + winner.value());
            }
            else if (result.a == 1) {
                winner = cases[1];
                System.out.println("Case 2 won: " + winner.value());
            }
            else {
                System.out.println("Default case won!");
            }
        }
    }
}

