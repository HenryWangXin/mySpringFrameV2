package wx.framework.bean.support;

import wx.framework.config.WxBeanDefinition;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WxBeanDefinitionReader {

    private List<String> registyBeanClass = new ArrayList<String>();

    public WxBeanDefinitionReader(String locatioin){
        Properties configContext = new Properties();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locatioin);//config.getInitParameter("contextConfigLocation")
        try {
            configContext.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null == is){
                return;
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        doScanner(configContext.getProperty("scanPackage"));//wx.demo
    }

    private void doScanner(String scanPackage) {
        try {
            URL url = this.getClass().getClassLoader()
                    .getResource("/" + scanPackage.replaceAll("\\.", "/"));
            File classDir = new File(url.getFile());
            for (File file : classDir.listFiles()) {
                if (file.isDirectory()) {
                    doScanner(scanPackage + "." + file.getName());
                } else {
                    if (file.getName().endsWith(".class")) {
                        String className = (scanPackage + "." + file.getName()).replace(".class", "");
                        registyBeanClass.add(className);
                    }
                }
            }
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    public List<WxBeanDefinition> loadBeanDefinitions() {
        List<WxBeanDefinition> result = new ArrayList<WxBeanDefinition>();
        try {
            for (String className : registyBeanClass) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) {
                    continue;
                }
                result.add(doCreateBeanDefinition(StringUtil.toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));
                Class<?>[] interfaces = beanClass.getInterfaces();
                for(Class<?> i:interfaces){
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private WxBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName) {
        WxBeanDefinition beanDefinition = new WxBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setScope("single");//通过反射能够拿到；这里先写死
        //todo
        return beanDefinition;
    }


}

