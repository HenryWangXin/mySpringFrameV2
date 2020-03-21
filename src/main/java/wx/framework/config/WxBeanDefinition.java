package wx.framework.config;

public class WxBeanDefinition {
    private String beanClassName;
    private boolean lazyInit = false;//
    private String factoryBeanName;
    private String scope;
    public String getBeanClassName() {
        return beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public String getScope() {
        return scope;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
