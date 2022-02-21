package cn.edcheung.springskills.web.validation.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description List转Tree工具类
 *
 * @author Edward Cheung
 * @date 2020/11/4
 * @since JDK 1.8
 */
public class TreeUtil {

    public static void listToTree(List<Map<String, Object>> mapList, String key, String pkey) {
        children(mapList, key, pkey);
        Iterator<Map<String, Object>> iterable = mapList.iterator();
        while (iterable.hasNext()) {
            Map<String, Object> map = iterable.next();
            if (map.get("node") != null) {
                map.remove("node");
                iterable.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void children(List<Map<String, Object>> mapList, String k1, String k2) {
        for (Map<String, Object> map : mapList) {
            map.put("children", new ArrayList<Map<String, Object>>(8));
            for (Map<String, Object> mp : mapList) {
                // pkey's value
                String pv = mp.get(k2).toString();
                if (!"0".equals(pv) && pv.equals(map.get(k1).toString())) {
                    mp.put("node", 1);
                    ((List<Map<String, Object>>) map.get("children")).add(mp);
                }
            }
        }
    }
}
