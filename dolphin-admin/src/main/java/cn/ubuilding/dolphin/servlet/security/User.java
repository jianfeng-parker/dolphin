package cn.ubuilding.dolphin.servlet.security;

/**
 * @author Wu Jianfeng
 * @since 16/3/2 08:28
 */

public class User {

    private int id;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 用户名称
     */
    private String name;


    public User(int id, String account, String name) {
        this.id = id;
        this.account = account;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

}
