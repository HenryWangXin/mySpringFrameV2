package wx.demo;

import wx.annotation.WXAutowired;
import wx.annotation.WXController;
import wx.annotation.WXRequestMapping;
import wx.annotation.WXRequestParam;
import wx.framework.webmvc.WxModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@WXController
@WXRequestMapping("wx")
public class DemoController {
    @WXAutowired
    private IDemoService demoService;
    @WXRequestMapping("query")
    public String query(HttpServletRequest request, HttpServletResponse resp, @WXRequestParam("name") String name) {
        String str = "";
        String value = demoService.get(name);
        try {
            str = "hello,mySpring: "+value+"--"+System.currentTimeMillis();

        } catch (Exception e) {
            StringWriter out = new StringWriter();
            e.printStackTrace(new PrintWriter(out, true));
            System.out.println(out.toString());
        }
        return str;
    }

    @WXRequestMapping("page")
    public WxModelAndView page(HttpServletRequest request, HttpServletResponse resp, @WXRequestParam("name") String name) {
        WxModelAndView mv = new WxModelAndView("HomePage");
        String value = demoService.get(name);
        try {
            Map<String,String> data = new HashMap<String,String>();
            String str = "hello,mySpring: "+value+"--"+System.currentTimeMillis();
            data.put("name",str);
        } catch (Exception e) {
            StringWriter out = new StringWriter();
            e.printStackTrace(new PrintWriter(out, true));
            System.out.println(out.toString());
        }
        return mv;
    }

}
