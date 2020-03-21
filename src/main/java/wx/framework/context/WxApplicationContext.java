package wx.framework.context;

import wx.annotation.WXAutowired;
import wx.annotation.WXController;
import wx.annotation.WxService;
import wx.framework.bean.support.StringUtil;
import wx.framework.bean.support.WxBeanDefinitionReader;
import wx.framework.bean.support.WxBeanPostProcessor;
import wx.framework.bean.support.WxDefaultListableBeanFactory;
import wx.framework.config.ObjectFactory;
import wx.framework.config.WxBeanDefinition;
import wx.framework.config.WxBeanWraper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WxApplicationContext extends WxDefaultListableBeanFactory {
    private WxBeanDefinitionReader reader;
    private String locatioin;
    public Boolean allowCircularReferences = true;

    private Map<String,Object> factoryBeanInstanceSet = new ConcurrentHashMap<String, Object>();
    //private Map<String, WxBeanWraper> factoryBeanInstanceCache = new ConcurrentHashMap<String, WxBeanWraper>();

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(256);
    private final Map<String, ObjectFactory> singletonFactories = new HashMap<String, ObjectFactory>(16);
    private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);

    public WxApplicationContext(String locatioin){
        this.locatioin = locatioin;
        try {
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void refresh() throws Exception{

        reader = new WxBeanDefinitionReader(this.locatioin);

        List<WxBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        doRegisterBeanDefinition(beanDefinitions);

        finishBeanFactoryInitialization();
    }

    private void finishBeanFactoryInitialization() {
        for(Map.Entry<String,WxBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }
    }

    public Object getBean(String beanName) {
        if(singletonObjects.containsKey(beanName)){
            return singletonObjects.get(beanName);
        }
        if(earlySingletonObjects.containsKey(beanName)){
            return earlySingletonObjects.get(beanName);
        }
        if(singletonFactories.containsKey(beanName)){
            ObjectFactory of = singletonFactories.get(beanName);
            Object proxy = makeFactProxy(of);
            earlySingletonObjects.put(beanName,proxy);
            singletonFactories.remove(beanName);
            return proxy;
        }

        WxBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        try {
            WxBeanPostProcessor beanPostProcessor = new WxBeanPostProcessor();
            Object instance = instantiateBean(beanDefinition);
            beanPostProcessor.postProcessAfterInitalization(instance, beanName);

            WxBeanWraper beanWraper = new WxBeanWraper(instance);
            if("single".equals(beanDefinition.getScope()) && allowCircularReferences){
                ObjectFactory of = new ObjectFactory(beanWraper);
                this.singletonFactories.put(beanName,of);
            }

            beanPostProcessor.postProcessAfterInitalization(instance,beanName);
            populateBean(beanName,instance);
            //aop
            Object proxy = makeBeanProxy(instance);
            singletonObjects.put(beanName,proxy);
            return  proxy;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object makeFactProxy(ObjectFactory of) {
        return  of.getWraper().getWrappedInstance();
    }

    /**
     * 动态代理
     * @param instance
     * @return
     */
    private Object makeBeanProxy(Object instance) {
        return instance;
    }

    private void populateBean(String beanName, Object instance) {
        Class clazz = instance.getClass();
        if(!(clazz.isAnnotationPresent(WXController.class) || clazz.isAnnotationPresent(WxService.class))){
            return;
        }
        Field[] fileds = clazz.getDeclaredFields();
        for (Field field : fileds) {
            if (!field.isAnnotationPresent(WXAutowired.class)) {
                continue;
            }
            WXAutowired wxAutowired = field.getAnnotation(WXAutowired.class);
            String autoWiredbeanName = wxAutowired.value().trim();
            if (null == autoWiredbeanName || "".equals(autoWiredbeanName)) {
                autoWiredbeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                field.set(instance, getBean(autoWiredbeanName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private Object instantiateBean(WxBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            if(this.factoryBeanInstanceSet.containsKey(className)){
                instance = factoryBeanInstanceSet.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.factoryBeanInstanceSet.put(beanDefinition.getFactoryBeanName(),instance);
            }
            return  instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("wx.demo.IDemoService");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(clazz);
    }

    public Object getBean(Class<?> beanClass) {
        return getBean(StringUtil.toLowerFirstCase(beanClass.getSimpleName()));
    }

    private void doRegisterBeanDefinition(List<WxBeanDefinition> beanDefinitions) throws Exception {
        for (WxBeanDefinition beanDefinition : beanDefinitions){
            if(super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception(beanDefinition.getBeanClassName()+":"+" is exists");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }

}
