package cn.edcheung.springskills.io.nettyapp.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description ServerProperties
 *
 * @author Edward Cheung
 * @date 2023/3/15
 * @since JDK 1.8
 */
@Component
@ConfigurationProperties(prefix = "server")
public class ServerProperties {

    private String loginIp;
    private int loginPort;
    private String loginName;
    private String loginPassword;
    private int connectTimeout;
    private int reConnectTime;
    private int heartbeatTime;

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public int getLoginPort() {
        return loginPort;
    }

    public void setLoginPort(int loginPort) {
        this.loginPort = loginPort;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReConnectTime() {
        return reConnectTime;
    }

    public void setReConnectTime(int reConnectTime) {
        this.reConnectTime = reConnectTime;
    }

    public int getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(int heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }
}
