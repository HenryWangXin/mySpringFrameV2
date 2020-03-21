package wx.framework.webmvc;

import java.util.Map;

public class WxModelAndView {
    private String viewName;
    private Map<String,?> model;
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }

    public WxModelAndView(String viewName) {
        this(viewName,null);
    }

    public WxModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }


}
