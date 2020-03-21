package wx;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<String,String>();
        Map<String,String> map1 = new HashMap<String,String>();
        map1.put("aaa","999");
        map.put("aaa","555");
        map.put("bbb","333");
        map.put("bbb","2222");
        map.putAll(map1);
        System.out.println(map);

    }
}
