package cn.ubuilding.dolphin.controller;

import cn.ubuilding.dolphin.service.DolphinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Wu Jianfeng
 * @since 16/2/26 19:53
 */

@Controller
public class DolphinController {

    @Autowired
    private DolphinService service;

    @ResponseBody
    @RequestMapping(value = "/test")
    public Object getTest() {
        return service.getHost() + ":" + service.getPort() + ":" + service.isOk();
    }

}
