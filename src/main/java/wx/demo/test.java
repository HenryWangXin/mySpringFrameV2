package wx.demo;

import wx.demo.DemoService;
import wx.framework.context.WxApplicationContext;

public class test {
    public static void main(String[] args) {
        WxApplicationContext context = new WxApplicationContext("application.properties");
        System.out.println("[wangxin]-----:"+context.getBean("demoService"));
        System.out.println("[wangxin]-----:"+context.getBean(DemoService.class));
    }
}
