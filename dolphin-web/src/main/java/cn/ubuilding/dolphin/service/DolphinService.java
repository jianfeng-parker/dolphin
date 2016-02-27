package cn.ubuilding.dolphin.service;

/**
 * @author Wu Jianfeng
 * @since 16/2/26 20:46
 */

//@Service
public class DolphinService {

    //    @Value("${redis.host}")
    private String host;

    //    @Value("${redis.port}")
    private int port;

    private boolean ok;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
