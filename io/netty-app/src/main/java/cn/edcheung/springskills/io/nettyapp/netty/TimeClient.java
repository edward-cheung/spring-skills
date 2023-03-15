package cn.edcheung.springskills.io.nettyapp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // 使用默认端口
            }
        }
        new TimeClient().connect("127.0.0.1", port);
    }

    public void connect(String host, int port) throws Exception {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    // TCP参数，立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。
                    // 该值设置Nagle算法的启用，改算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，
                    // 如果需要发送一些较小的报文，则需要禁用该算法。Netty默认禁用该算法，从而最小化报文传输延时。
                    .option(ChannelOption.TCP_NODELAY, true)
                    // Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。
                    // 可以将此功能视为TCP的心跳机制，需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
                    .option(ChannelOption.SO_KEEPALIVE, false)
                    // Netty参数，连接超时毫秒数，默认值30000毫秒即30秒。
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                    // Socket参数，TCP数据发送缓冲区大小。
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    // Socket参数，TCP数据接收缓冲区大小。
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            // 发起异步连接操作
            ChannelFuture future = bootstrap.connect(host, port).sync();
            // 等待客户端链路关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }
}
