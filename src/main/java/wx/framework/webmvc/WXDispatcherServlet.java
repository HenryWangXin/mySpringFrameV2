package wx.framework.webmvc;

import wx.annotation.WXController;
import wx.annotation.WXRequestMapping;
import wx.demo.DemoService;
import wx.demo.Test2Service;
import wx.demo.TestService;
import wx.framework.config.WxBeanDefinition;
import wx.framework.context.WxApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WXDispatcherServlet extends HttpServlet {

    private String LOCATION = "contextConfigLocation";

    private List<WxHandlerMapping> handlerMappings = new ArrayList<WxHandlerMapping>();
    private Map<WxHandlerMapping,WxHandlerAdapter> handlerAdapters = new HashMap<WxHandlerMapping, WxHandlerAdapter>();
    private List<WxViewResolver> viewResolvers = new ArrayList<WxViewResolver>();
    private WxApplicationContext context;


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("404");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("404");
        }
    }

    /**
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        if(req.getRequestURI().endsWith(".ico")){
            return;
        }
        WxHandlerMapping handler = getHandler(req);
        if(handler == null){
            processDispatchResult(req,resp,new WxModelAndView("404"));
            return;
        }
        WxHandlerAdapter ha = getHandlerAdapter(handler);

        WxModelAndView mv = ha.handle(req,resp,handler);

        processDispatchResult(req,resp,mv);


    }

    private WxHandlerAdapter getHandlerAdapter(WxHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return null;}
        WxHandlerAdapter ha = this.handlerAdapters.get(handler);
        return ha;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, WxModelAndView mv) throws Exception {
        if(null == mv){ return; }
        if(this.viewResolvers.isEmpty()){return;}
        if(this.viewResolvers != null){
            for(WxViewResolver viewResolver :this.viewResolvers){
                WxView view = viewResolver.resolveViewName(mv.getViewName());
                if(null != view){
                    view.render(mv.getModel(),req,resp);
                    return;
                }
            }
        }
    }

    private WxHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){
            return null;
        }
        String url = req.getRequestURI();
        String cotextPath = req.getContextPath();
        url = url.replace(cotextPath,"").replaceAll("/+","/");

        for(WxHandlerMapping handler :this.handlerMappings){
            if(handler.getPattern().equals(url)){
                return  handler;
            }
        }
        return null;
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        //config.getInitParameter(LOCATION) -- application.properties
        context = new WxApplicationContext(config.getInitParameter(LOCATION));

        System.out.println("[wangxin]--------Test2Service.class---:"+System.identityHashCode(context.getBean(Test2Service.class)));
        Test2Service t2 = (Test2Service)context.getBean(Test2Service.class);
        System.out.println("[wangxin]------[Test2Service.clas->t]-----:"+t2.get("daf"));

        System.out.println("[wangxin]---------TestService.class--:"+System.identityHashCode(context.getBean(TestService.class)));
        TestService t = (TestService)context.getBean(TestService.class);
        System.out.println("[wangxin]-------[t->Test2Service.clas]----:"+t.get("asdf"));

        initStrategies(context);
        //initViewResolvers(context);

    }

    private void initStrategies(WxApplicationContext context) {
        //9大组件 文件上传，主题，
        initHandlerMappings(context);

        initHandlerAdapters(context);

        initViewResolvers(context);
        System.out.println("wwwwww_xxxxxxxx===="+this.handlerMappings);
        System.out.println("wwwwww_xxxxxxxx===="+this.handlerAdapters);
        System.out.println("wwwwww_xxxxxxxx===="+this.viewResolvers);

    }

    private void initHandlerMappings(WxApplicationContext context) {
        Map<String, WxBeanDefinition> beanDefinitionMap = context.getBeanDefinitionMap();
        try {
            for(Map.Entry<String, WxBeanDefinition> entry: beanDefinitionMap.entrySet()){
                String beanName = entry.getKey();
                //WxBeanDefinition bd = entry.getValue();
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if(!clazz.isAnnotationPresent(WXController.class)){
                    continue;
                }
                String reqUrl = "";
                if (clazz.isAnnotationPresent(WXRequestMapping.class)) {//mvc mapping类的
                    WXRequestMapping wxRequestMapping = clazz.getAnnotation(WXRequestMapping.class);
                    reqUrl = "/"+wxRequestMapping.value();
                }
                //mvc mapping 方法的
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(WXRequestMapping.class)) {
                        WXRequestMapping wxRequestMapping = method.getAnnotation(WXRequestMapping.class);
                        reqUrl = (reqUrl + "/" + wxRequestMapping.value()).replaceAll("/+", "/");
                        this.handlerMappings.add(new WxHandlerMapping(controller,method,reqUrl));
                        //handlerMap.put(reqUrl, method);
                        // System.out.println("url mapp key:" + reqUrl + " value:" + method);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initHandlerAdapters(WxApplicationContext context) {
        for(WxHandlerMapping handlerMapping : this.handlerMappings){
            this.handlerAdapters.put(handlerMapping,new WxHandlerAdapter());
        }
    }
    private void initViewResolvers(WxApplicationContext context) {
        String templatRoot = "src/main/java/wx/jsp";
        File templateDir = new File(templatRoot);
        for(File template : templateDir.listFiles()){
            System.out.println(template);
            this.viewResolvers.add(new WxViewResolver(templatRoot));
        }
    }

}
