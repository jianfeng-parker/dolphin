package cn.ubuilding.dolphin.servlet.security;

import org.springframework.stereotype.Service;

/**
 * @author Wu Jianfeng
 * @since 16/3/2 08:36
 * // TODO 登录模块接入SSO
 */

@Service
public class DolphinUserCas {

    public User getUser(String account, String password) {
        if ("parker".equals(account) || "wjf".equals(account)) // TODO 登录逻辑后续实现
            return new User(1001, account, "吴建峰-" + account);
        else return null;
    }
}
