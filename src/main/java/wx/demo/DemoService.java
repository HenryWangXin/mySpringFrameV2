package wx.demo;

import wx.annotation.WxService;

@WxService
public class DemoService implements IDemoService {
    public String get(String str) {
        return "DemoServiceDeal---"+str;
    }
}
