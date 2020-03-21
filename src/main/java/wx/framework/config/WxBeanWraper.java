package wx.framework.config;

public class WxBeanWraper {
    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    private Object wrappedInstance;

    public Class<?> getWrappedClass() {
        return wrappedClass;
    }

    private Class<?> wrappedClass;

    public WxBeanWraper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }
}
