package cn.edcheung.springskills.web.validation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * HttpServletRequest帮助类
 */
public class RequestUtil {

    private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

    private static final String PARAM_SEP = ",";

    private RequestUtil() {
    }

    /**
     * 获取HttpServletRequest
     * 注:RequestContextHolder底层使用本地线程方式绑定.当开启多线程时 不能获取 RequestAttributes,
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    public static String getURI(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        int start = uri.indexOf(contextPath);
        return uri.substring(start + contextPath.length());
    }


    public static Map<String, String> parseQueryString(String s) {
        if (s == null) {
            return Collections.emptyMap();
        }
        Map<String, String> ht = new HashMap<>();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                continue;
            }
            String key = pair.substring(0, pos);
            String val = pair.substring(pos + 1);
            if (ht.containsKey(key)) {
                String oldVals = ht.get(key);
                ht.put(key, oldVals + PARAM_SEP + val);
            } else {
                ht.put(key, val);
            }
        }
        return ht;
    }

    /**
     * 获取访问者IP
     * <p>
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     * <p>
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
     * 如果还不存在则调用Request.getRemoteAddr()。
     *
     * @param request 请求体
     * @return RemoteAddr
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getIpAddr() {
        String ipAddr = "";
        try {
            HttpServletRequest httpServletRequest = getHttpServletRequest();
            if (httpServletRequest != null) {
                ipAddr = getIpAddr(httpServletRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipAddr;
    }


    /**
     * 获取发起request请求的客户端的IP地址(IPV4)，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * <p>
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * <p>
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getRemoteIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.hasText(ip)) {
            return ip;
        }
        String[] newIps = ip.split(PARAM_SEP);
        return newIps[0];
    }
}