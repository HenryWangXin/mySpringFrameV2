package wx.framework.bean.support;

public class WxBeanPostProcessor {
    public Object postProcessBeforeInitalization(Object bean,String beanName) throws Exception{
        return  bean;
    }
    public  Object postProcessAfterInitalization(Object bean,String beanName) throws Exception{
        return  bean;
    }
}
