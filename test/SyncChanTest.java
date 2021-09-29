import go.builtin.channel.Channel;
import go.builtin.Int8;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class SyncChanTest {

    public static volatile boolean run = true;

    public static void main(String[] args) throws InterruptedException {

        Channel<Int8> channel = Channel.make(Int8.goClass);

        Runnable sender = () -> {
            int sent = 0;
            Random random = new Random();
            Int8 byt = new Int8((byte) 0);
            try {
                while (SyncChanTest.run) {
                    byt.assign((byte) random.nextInt());
                    //System.out.println("Thread " + Thread.currentThread().getId() + " will now send " + byt);
                    channel.send(byt.clone());
                    //System.out.println("Thread " + Thread.currentThread().getId() + " has sent.");
                    sent++;
                    //Thread.sleep(random.nextInt(1000));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Thread " + Thread.currentThread().getId() + " sent a total of " + sent);
        };

        Runnable receiver = () -> {
            int received = 0;
            try {
                while (SyncChanTest.run) {
                    //System.out.println("Thread " + Thread.currentThread().getId() + " will now receive.");
                    Int8 byt = channel.receive();
                    //System.out.println("Thread " + Thread.currentThread().getId() + " received " + byt);
                    received++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Thread " + Thread.currentThread().getId() + " received a total of " + received);
        };

        ExecutorService es = Executors.newFixedThreadPool(2);
        es.submit(sender);
        es.submit(receiver);

        while (run) LockSupport.park();
    }
}
