package wx.demo;


import wx.annotation.WXAutowired;
import wx.annotation.WxService;

@WxService
public class Test2Service implements ITest2Service {
    @WXAutowired("testService")
    ITestService testService;

    public String get(String str) {
        return String.valueOf(System.identityHashCode(testService));
    }
}
