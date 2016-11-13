package cn.ubuilding.dolphin.web.response;

/**
 * @author Wu Jianfeng
 * @since 16/3/8 21:57
 */

public class DolphinResponse {

    private int code;

    private String msg;

    private Object data;

    public DolphinResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public DolphinResponse(int code, String msg) {
        this(code, msg, null);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public enum ResponseCode{

    }
}
