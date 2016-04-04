package cn.ubuilding.dolphin.servlet.interceptor;

import cn.ubuilding.dolphin.servlet.constants.GlobalConstants;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Wu Jianfeng
 * @since 16/3/2 07:59
 */

public class DolphinInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object session = WebUtils.getSessionAttribute(request, GlobalConstants.USER_SESSION);
        if (null != session) return true;

        else {
            WebUtils.setSessionAttribute(request, GlobalConstants.PRE_REQUEST_URI, request.getRequestURI());
            response.sendRedirect("/login");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
