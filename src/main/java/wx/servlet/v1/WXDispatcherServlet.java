package wx.servlet.v1;

import wx.annotation.WXAutowired;
import wx.annotation.WXController;
import wx.annotation.WXRequestMapping;
import wx.annotation.WxService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class WXDispatcherServlet extends HttpServlet {
    //收集扫描出来的类
    private Map<String, Object> scanClassMap = new HashMap<String, Object>();
    private Map<String, Object> singletonObjects = new HashMap<String, Object>();
    private Map<String, Object> handlerMap = new HashMap<String, Object>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("页面报错");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("页面报错");
        }
    }

    /**
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String uri = req.getRequestURI();
            String contextPath = req.getContextPath();
            String url = uri.replace(contextPath, "").replaceAll("/+", "/");
            if (!handlerMap.containsKey(url)) {
                resp.getWriter().write("404 找不到页面");
                return;
            }
            Method method = (Method) handlerMap.get(url);
            Map<String,String[]> params = req.getParameterMap();
            List<Object> argList = new ArrayList<Object>();
            argList.add(req);
            argList.add(resp);
            argList.add(params.get("name")[0]);
            method.invoke(singletonObjects.get(method.getDeclaringClass().getName()), argList.toArray());
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter out = new StringWriter();
            e.printStackTrace(new PrintWriter(out, true));
            System.out.println(out.toString());

            String uri = req.getRequestURI();
            System.out.println(uri);
        }
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        InputStream is = null;
        try {
            Properties configContext = new Properties();
            is = this.getClass().getClassLoader().getResourceAsStream(config.getInitParameter("contextConfigLocation"));
            configContext.load(is);
            String scanPackage = configContext.getProperty("scanPackage");
            System.out.println("wangxin:" + scanPackage);
            doScanner(scanPackage);
            //IOC
            for (String className : scanClassMap.keySet()) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(WXController.class)) {
                    singletonObjects.put(className, clazz.newInstance());
                    //组装 springMvc mapping 关系
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
                            handlerMap.put(reqUrl, method);
                            System.out.println("url mapp key:" + reqUrl + " value:" + method);
                        }
                    }
                }
                if (clazz.isAnnotationPresent(WxService.class)) {
                    WxService ANService = clazz.getAnnotation(WxService.class);
                    String beanName = ANService.value();
                    if (null == beanName || "".equals(beanName)) {
                        beanName = clazz.getName();
                    }
                    Object instance = clazz.newInstance();
                    singletonObjects.put(beanName, instance);
                    for (Class<?> i : clazz.getInterfaces()) {
                        singletonObjects.put(i.getName(), instance);
                    }
                }
            }
            //DI
            for (Object obj : singletonObjects.values()) {
                if (null == obj) {
                    return;
                }
                Class clazz = obj.getClass();
                if (clazz.isAnnotationPresent(WXController.class)) {
                    Field[] fileds = clazz.getDeclaredFields();
                    for (Field field : fileds) {
                        if (!field.isAnnotationPresent(WXAutowired.class)) {
                            continue;
                        }
                        WXAutowired wxAutowired = field.getAnnotation(WXAutowired.class);
                        String beanName = wxAutowired.value();
                        if (null == beanName || "".equals(beanName)) {
                            beanName = field.getType().getName();
                        }
                        field.setAccessible(true);
                        try {
                            field.set(singletonObjects.get(clazz.getName()), singletonObjects.get(beanName));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            System.out.println("扫描结果：" + scanClassMap);
            System.out.println("单例池为：" + singletonObjects);
            System.out.println("请求方法对应map为：" + handlerMap);
        } catch (Exception e) {
            StringWriter out = new StringWriter();
            e.printStackTrace(new PrintWriter(out, true));
            System.out.println(out.toString());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader()
                .getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (file.getName().endsWith(".class")) {
                    String className = (scanPackage + "." + file.getName()).replace(".class", "");
                    scanClassMap.put(className, "");
                }
            }
        }
    }

}
