package wx.framework.webmvc;

import wx.framework.core.WxBeanFactory;

import java.io.File;

public class WxViewResolver {
    private String SUFFIX = ".html";
    private File templateRootDir;
    private String viewName;

    public WxViewResolver(String templateRootDir) {
        //String temlateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootDir);
    }

    public WxView resolveViewName(String viewName) throws Exception{
        this.viewName = viewName;
        if(null == viewName || "".equals(viewName)){
            return null;
        }
        viewName = viewName.endsWith(SUFFIX)?viewName : viewName+SUFFIX;
        File template = new File((templateRootDir.getPath() +"/"+viewName).replaceAll("/+","/"));
        return new WxView(template);
    }
}

