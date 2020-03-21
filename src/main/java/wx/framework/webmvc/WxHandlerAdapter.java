package wx.framework.webmvc;

import wx.annotation.WXRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WxHandlerAdapter {
    /*public boolean supports(Object handler){
        //return
    }*/
    public WxModelAndView handle(HttpServletRequest req, HttpServletResponse resp,Object handler) throws Exception {
        WxHandlerMapping handlerMapping = (WxHandlerMapping) handler;
        Map<String, Integer> paramMapping = new HashMap<String, Integer>();
        //二维数组，每个参数由多个数组
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof WXRequestParam) {
                    String parmName = ((WXRequestParam) a).value();
                    if (!"".equals(parmName.trim())) {
                        paramMapping.put(parmName, i);
                    }
                }
            }
        }
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();

        Map<String, String[]> reqParameterMap = req.getParameterMap();

        Object[] paramValues = new Object[paramTypes.length];

        paramValues[0] = req;
        paramValues[1] = resp;

        for (Map.Entry<String, String[]> param : reqParameterMap.entrySet()) {
            if (!paramMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramMapping.get(param.getKey());
            paramValues[index] = Arrays.toString(param.getValue());
        }

        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);
        if(result == null){
            return  null;
        }
        if(result instanceof String){
            resp.getWriter().write(String.valueOf(result));
            return null;
        }

        if(handlerMapping.getMethod().getReturnType() == WxModelAndView.class){
            return (WxModelAndView) result;
        }
        return null;
    }
}
