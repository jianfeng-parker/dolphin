package cn.ubuilding.dolphin.web.controller;

import cn.ubuilding.dolphin.servlet.security.DolphinUserCas;
import cn.ubuilding.dolphin.servlet.security.User;
import cn.ubuilding.dolphin.servlet.constants.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Wu Jianfeng
 * @since 16/2/28 20:33
 */

@Controller
public class LoginController {

    @Autowired
    private DolphinUserCas cas;

    @RequestMapping(value = "/login")
    public ModelAndView login(String account, String password, boolean submit, HttpSession session) {
        ModelAndView mal = new ModelAndView();
        if (submit) { // 登录
            // login magic
            User user = cas.getUser(account, password);
            if (null == user) {
                mal.setViewName("login");
                mal.addObject("showNotice", true);
                mal.addObject("notice", "wrong username or password");

            } else {
                // 写session
                session.setAttribute(GlobalConstants.USER_SESSION, user);
                // 获取登录成功后需要跳转的URI
                Object preURI = session.getAttribute(GlobalConstants.PRE_REQUEST_URI);
                String uri = null == preURI ? "/" : preURI.toString();
                session.removeAttribute(GlobalConstants.PRE_REQUEST_URI);

                mal.setView(new RedirectView(uri));
            }

        } else { // 渲染登录界面
            mal.setViewName("login");
            mal.addObject("showNotice", false);
        }
        return mal;
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) throws IOException {
        session.removeAttribute(GlobalConstants.USER_SESSION);
//        response.sendRedirect("/login");
        return "redirect:login";
    }

}
