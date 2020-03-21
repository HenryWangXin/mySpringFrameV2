package wx.framework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Pattern;

public class WxView {
    public static final String DEFAULT_CONTENT_TYPE ="text/html; charset=utf-8";
    private File viewFile;
    public WxView(File viewFile) {
        this.viewFile = viewFile;
    }
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception{
        StringBuilder sb = new StringBuilder();
        RandomAccessFile ra = new RandomAccessFile(this.viewFile,"r");
        try{
            String line = null;
            while(null != (line = ra.readLine())){
                line = new String(line.getBytes("ISO-8859-1"),"utf-8");
                //todo
                line +="wangxin \n";
                System.out.println(line);
            }
            sb.append(line);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ra.close();
        }
        response.getWriter().write(sb.toString());
    }

}
