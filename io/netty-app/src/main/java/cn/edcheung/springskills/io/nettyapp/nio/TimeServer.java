package cn.edcheung.springskills.io.nettyapp.nio;

/**
 * 使用NIO编程的优点：
 * (1)客户端发起的连接操作是异步的，可以通过在多路复用器注册OP CONNECT等待后续结果，不需要像之前的客户端那样被同步阻塞。
 * (2)SocketChannel的读写操作都是异步的，如果没有可读写的数据它不会同步等待， 直接返回，这样/O通信线程就可以处理其他的链路，不需要同步等待这个链路可用。
 * (3)线程模型的优化：由于JDK的Selector在Linux等主流操作系统上通过epoll实现，它没有连接句柄数的限制（只受限于操作系统的最大句柄数或者对单个进程的句柄限制)，
 * 这意味着一个Selector线程可以同时处理成千上万个客户端连接，而且性能不会随着客户端的增加而线性下降。因此，它非常适合做高性能、高负载的网络服务器。
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
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "TimeServer-NIO").start();
    }
}
