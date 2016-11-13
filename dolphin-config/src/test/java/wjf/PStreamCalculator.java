package wjf;


/**
 * @author Wu Jianfeng
 * @since 16/5/9 07:02
 */

public class PStreamCalculator {
    public static void main(String[] args) {
        new Thread(new Plus()).start();
        new Thread(new Multiply()).start();
        new Thread(new Div()).start();
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                Msg msg = new Msg();
                msg.i = i;
                msg.j = j;
                msg.orgStr = "(i+j)*i/2 = ";
                Plus.queue.add(msg);
            }
        }
    }
}
