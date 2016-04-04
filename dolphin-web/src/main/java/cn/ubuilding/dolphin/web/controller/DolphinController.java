package cn.ubuilding.dolphin.web.controller;

import cn.ubuilding.dolphin.constant.TabDefinition;
import cn.ubuilding.dolphin.service.DolphinService;
import cn.ubuilding.dolphin.servlet.security.User;
import cn.ubuilding.dolphin.servlet.constants.GlobalConstants;
import cn.ubuilding.dolphin.web.pojo.ConfigInfo;
import cn.ubuilding.dolphin.web.response.DolphinResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/2/26 19:53
 */

@Controller
@RequestMapping("/")
public class DolphinController {

    @Autowired
    private DolphinService service;

    /**
     * 渲染系统首页
     */
    @RequestMapping(value = {"", "index"})
    public ModelAndView index(HttpSession session) {
        return buildMav(TabDefinition.Introduction.getViewName(), TabDefinition.Introduction.getCode(), session);
    }

    /**
     * 渲染主页某个tab界面
     */
    @RequestMapping(value = "settings")
    public ModelAndView settingsList(HttpServletRequest request) {
        int active = null == request.getParameter("active") ? TabDefinition.Introduction.getCode() : Integer.parseInt(request.getParameter("active"));
        String viewName = TabDefinition.getViewName(active);
        ModelAndView mav= buildMav(StringUtils.isEmpty(viewName) ? TabDefinition.Introduction.getViewName() : viewName,
                active, request.getSession());
        List<ConfigInfo> list = new ArrayList<>();
        list.add(new ConfigInfo("redis.host","192.168.1.105","Redis服务主机"));
        list.add(new ConfigInfo("redis.port","6317","Redis服务端口"));

        mav.addObject("configs",list);
        return mav;
    }

    /**
     * 保存设置数据
     */
    @ResponseBody
    @RequestMapping(value = "/api/save/{env}")
    public Object settings(@PathVariable("env") int env, ConfigInfo config) {
        System.out.println(config);
        return new DolphinResponse(1301, "登录已过期");
    }

    private ModelAndView buildMav(String viewName, int active, HttpSession session) {
        ModelAndView mav = new ModelAndView(viewName);
        User user = (User) session.getAttribute(GlobalConstants.USER_SESSION);
        mav.addObject("username", user.getName());
        mav.addObject("tabs", TabDefinition.values());
        mav.addObject("active", active);
        return mav;
    }

}
