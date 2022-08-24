package cn.edcheung.springskills.io.nettyapp.bio2;

import cn.edcheung.springskills.io.nettyapp.bio.TimeServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 伪异步BIO编程：
 * 伪异步I/O通信框架采用了线程池实现，因此避免了为每个请求都创建一个独立线程造成的线程资源耗尽问题。
 * 但是由于它底层的通信依然采用同步阻塞模型，因此无法从根本上解决问题。
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // 使用默认端口
            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            // 创建I/O任务线程池
            TimeServerHandlerExecutePool executor = new TimeServerHandlerExecutePool(50, 10000);
            Socket socket = null;
            while (true) {
                socket = server.accept();
                executor.execute(new TimeServerHandler(socket));
            }
        } catch (IOException e) {
            // 端口不合法或者被占用
            e.printStackTrace();
        } finally {
            if (server != null) {
                System.out.println("The time server close");
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                server = null;
            }
        }
    }
}
