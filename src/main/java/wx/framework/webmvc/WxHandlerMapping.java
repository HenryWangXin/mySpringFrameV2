package wx.framework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class WxHandlerMapping {
    private  Object controller;
    private Method method;
    private String pattern;

    public WxHandlerMapping(Object controller, Method method, String pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    public String getPattern() {
        return pattern;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }


}
