package wx.demo;


import wx.annotation.WXAutowired;
import wx.annotation.WxService;

@WxService
public class TestService implements ITestService {
    @WXAutowired("test2Service")
    ITest2Service test2Service;
    public String get(String str) {
        return String.valueOf(System.identityHashCode(test2Service));
    }
}
