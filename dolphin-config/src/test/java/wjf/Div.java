package wjf;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Wu Jianfeng
 * @since 16/5/9 07:11
 */

public class Div implements Runnable {
    public static BlockingDeque<Msg> queue = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    Msg msg = queue.take();
                    msg.i = msg.i / 2;
                    System.out.println(">>>>>>>>>>" + msg.orgStr + msg.i);
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {

        }
    }
}
