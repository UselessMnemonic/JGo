import go.builtin.channel.Channel;
import go.builtin.Int8;
import sun.misc.Signal;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static volatile boolean run = true;

    public static void main(String[] args) throws InterruptedException {
        Signal.handle(new Signal("INT"), sig -> Main.run = false);

        Channel<Int8> channel = Channel.make(Int8.goClass);

        Runnable sender = () -> {
            int sent = 0;
            Random random = new Random();
            Int8 byt = new Int8((byte) 0);
            while (Main.run) try {
                byt.assign((byte) random.nextInt());
                System.out.println("Thread " + Thread.currentThread().getId() + " will now send " + byt);
                channel.send(byt);
                sent++;
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException ignored) {}
            System.out.println("Thread " + Thread.currentThread().getId() + " sent a total of " + sent);
        };

        Runnable receiver = () -> {
            int received = 0;
            while (Main.run) {
                System.out.println("Thread " + Thread.currentThread().getId() + " received " + channel.receive());
                received++;
            }
            System.out.println("Thread " + Thread.currentThread().getId() + " received a total of " + received);
        };

        ExecutorService es = Executors.newFixedThreadPool(2);
        es.submit(sender);
        es.submit(receiver);
        es.shutdown();
        while(!es.awaitTermination(1, TimeUnit.MINUTES));
    }
}
