package cn.edcheung.springskills.io.nettyapp.spring;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Description NettyClient
 *
 * @author Edward Cheung
 * @date 2023/3/8
 * @since JDK 1.8
 */
@Component
public class NettyClient implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    @Resource
    private ServerProperties serverProperties;
    @Resource
    private NettyHandler nettyHandler;

    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Netty client 初始化配置");
        // 如果 threadTotal 为 0 就采用netty默认的线程池
        eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();
        this.bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .remoteAddress(serverProperties.getLoginIp(), serverProperties.getLoginPort())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, serverProperties.getConnectTimeout())
                .option(ChannelOption.SO_RCVBUF, 1 << 20)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // LineBasedFrameDecoder netty解码器，以换行符为结束标志的解码器。判断看是否有\n 或 \r\n，如果有就以此位置为结束位置
                        ch.pipeline().addLast(new LineBasedFrameDecoder(1 << 20));
                        // StringDecoder netty解码器。将接受到到的对象转换成字符串，然后流向下一个channelHandle
                        ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
                        // 添加客户端心跳检测机制
                        ch.pipeline().addLast(new IdleStateHandler(0, serverProperties.getHeartbeatTime(), 0, TimeUnit.MILLISECONDS));
                        ch.pipeline().addLast(nettyHandler);
                    }
                });
        logger.info("Netty client 初始化配置完成");
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Netty client 销毁连接");
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
        logger.info("Netty client 销毁连接完成");
    }

    public void connectServer() {
        bootstrap.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    logger.error("Failed to connect: " + future.cause());
                }
            }
        });
    }

}
