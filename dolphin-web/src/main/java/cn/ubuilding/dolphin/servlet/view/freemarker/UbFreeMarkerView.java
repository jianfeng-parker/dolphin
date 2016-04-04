package cn.ubuilding.dolphin.servlet.view.freemarker;

import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 16/3/1 21:06
 */

public class UbFreeMarkerView extends FreeMarkerView {
    private static final String CONTEXT_PATH = "ctx";

    public void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
        model.put(CONTEXT_PATH, request.getContextPath());
        super.exposeHelpers(model, request);
    }
}
