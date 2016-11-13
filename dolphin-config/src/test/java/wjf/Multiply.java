package wjf;


import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Wu Jianfeng
 * @since 16/5/9 07:08
 */

public class Multiply implements Runnable {

    public static BlockingDeque<Msg> queue = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        while (true) {
            try {
                Msg msg = queue.take();
                msg.i = msg.i * msg.j;
                Div.queue.add(msg);
            } catch (Exception e) {

            }
        }
    }
}
