package cn.edcheung.springskills.web.validation.util;


import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Description ServerIPUtil
 *
 * @author zhangyi
 * @date 2020/10/28
 * @since JDK 1.8
 */
public class ServerIPUtil {

    private static final String DEFAULT_SERVER_IP = "127.0.0.1";

    /**
     * 获取本地IP地址
     */
    public static String getLocalIP() {
        if (isWindowsOS()) {
            return getWindowsLocalIP();
        } else {
            return getLinuxLocalIP();
        }
    }

    /**
     * 判断操作系统是否是Windows
     */
    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }

    /**
     * 获取Linux下的IP地址
     */
    private static String getWindowsLocalIP() {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ip = DEFAULT_SERVER_IP;
        }
        return ip;
    }

    /**
     * 获取Linux下的IP地址
     */
    private static String getLinuxLocalIP() {
        String ip = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException ignored) {
        }
        return StringUtils.hasText(ip) ? DEFAULT_SERVER_IP : ip;
    }

}
