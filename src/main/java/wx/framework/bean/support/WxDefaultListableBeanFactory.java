package wx.framework.bean.support;

import wx.framework.config.WxBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WxDefaultListableBeanFactory {
    public Map<String, WxBeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }

    protected final Map<String, WxBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, WxBeanDefinition>();

    public void refresh() throws Exception{
    }
}
