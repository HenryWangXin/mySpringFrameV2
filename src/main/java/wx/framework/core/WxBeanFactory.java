package wx.framework.core;

public interface WxBeanFactory {
    public Object getBean(String beanName) throws Exception;

    public Object getBean(Class<?> beanClass) throws Exception;
}
