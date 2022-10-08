package cn.edcheung.springskills.io.nettyapp.aio;

/**
 * AIO编程， 同项目NIO编程，都没有完整的处理网络的半包读写
 * 对于AsynchronousServerSocketChannel和AsynchronousSocketChannel，它们都由JDK底层的线程池负责回调并驱动读写操作。
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
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer, "TimeServer-AIO").start();
    }
}
