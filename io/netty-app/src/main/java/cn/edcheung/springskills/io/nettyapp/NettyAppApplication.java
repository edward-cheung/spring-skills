package cn.edcheung.springskills.io.nettyapp;

import cn.edcheung.springskills.io.nettyapp.spring.NettyClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Resource;

@SpringBootApplication
public class NettyAppApplication implements CommandLineRunner {

    private static ConfigurableApplicationContext applicationContext;

    @Resource
    private NettyClient nettyClient;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(NettyAppApplication.class, args);
    }

    public static void shutdown() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        // 启动netty客户端监听
        nettyClient.connectServer();
    }

}
