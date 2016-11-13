package wjf;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Wu Jianfeng
 * @since 16/5/9 07:07
 */

public class Plus implements Runnable {

    public static BlockingQueue<Msg> queue = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        while (true) {
            try {
                Msg msg = queue.take();
                msg.j = msg.i + msg.j;
                Multiply.queue.add(msg);
            } catch (Exception e) {

            }
        }
    }
}
