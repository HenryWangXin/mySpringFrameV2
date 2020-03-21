package wx.framework.config;

public class ObjectFactory {
    public void setWraper(WxBeanWraper wraper) {
        this.wraper = wraper;
    }

    public WxBeanWraper getWraper() {
        return wraper;
    }

    public ObjectFactory(WxBeanWraper wraper) {
        this.wraper = wraper;
    }

    private WxBeanWraper wraper;


}
