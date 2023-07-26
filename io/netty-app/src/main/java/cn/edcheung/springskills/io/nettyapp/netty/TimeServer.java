package cn.edcheung.springskills.io.nettyapp.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // 使用默认端口
            }
        }
        new TimeServer().bind(port);
    }

    public void bind(int port) throws Exception {
        // 配置服务端的NIO线程组
        // 用于服务端接受客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用于进行SocketChannel的网络读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // Socket参数，服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。默认值，Windows为200，其他为128。
                    // 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接。
                    // 多个客户端同时来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理。
                    // syns queue：保存一个SYN已经到达，但三次握手还没有完成的连接。
                    // accept queue：保存三次握手已完成，内核正等待进程执行accept的调用的连接。
                    .option(ChannelOption.SO_BACKLOG, 64)
                    // Socket参数，TCP数据接收缓冲区大小。需要注意的是：当设置值超过64KB时，需要在绑定到本地端口前设置。
                    // 该值设置的是由ServerSocketChannel使用accept接受的SocketChannel的接收缓冲区。通常建议值为128K或者256K。
                    .option(ChannelOption.SO_RCVBUF, 2 >> 18)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChildChannelHandler());
            // 绑定端口，同步等待成功
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }
}
