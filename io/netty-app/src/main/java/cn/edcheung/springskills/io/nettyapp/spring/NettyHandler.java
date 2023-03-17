package cn.edcheung.springskills.io.nettyapp.spring;

import cn.edcheung.springskills.io.nettyapp.NettyAppApplication;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Description NettyHandler
 *
 * @author Edward Cheung
 * @date 2023/3/8
 * @since JDK 1.8
 */
@Component
@ChannelHandler.Sharable
public class NettyHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(NettyHandler.class);

    /**
     * client 与 server 心跳检测报文
     */
    private static final String COMMAND_HEART_BEAT = "HEARTBEAT \r\n";
    private static final String COMMAND_HEART_BEAT_ACK = "HEARTBEAT ACK";

    @Resource
    private ServerProperties serverProperties;
    @Resource
    private NettyClient nettyClient;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        if (s == null || s.length() == 0) {
            return;
        }
        String value = s.trim();
        if (value.length() < COMMAND_HEART_BEAT_ACK.length() || COMMAND_HEART_BEAT_ACK.equals(value)) {
            return;
        }
        if (value.startsWith("LOGIN")) {
            if ("LOGIN SUCCEED".equals(value)) {
                logger.info("Netty client 登录Server成功：" + value);
            } else {
                logger.error("Netty client 登录Server失败：" + value);
                NettyAppApplication.shutdown();
            }
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("接收数据: " + value);
        }
        // 处理数据
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().toString();
        logger.info("登录 (当前 channel.id = {})", channelId);
        String loginCommand = "LOGIN AS " + serverProperties.getLoginName() + ":" + serverProperties.getLoginPassword() + " \r\n";
        byte[] loginCommandByteArray = loginCommand.getBytes();
        ByteBuf loginCommandByteBuf = Unpooled.buffer(loginCommandByteArray.length);
        loginCommandByteBuf.writeBytes(loginCommandByteArray);
        ctx.writeAndFlush(loginCommandByteBuf);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String channelId = ctx.channel().id().toString();
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                byte[] heartBeatCommandByteArray = COMMAND_HEART_BEAT.getBytes(StandardCharsets.UTF_8);
                ByteBuf heartBeatCommandByteBuf = Unpooled.buffer(heartBeatCommandByteArray.length);
                heartBeatCommandByteBuf.writeBytes(heartBeatCommandByteArray);
                ctx.writeAndFlush(heartBeatCommandByteBuf);
                logger.info("心跳 (当前 channel.id = {})", channelId);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // 如果连接失败则由后台线程自动执行重连
        ctx.channel().eventLoop().schedule(() -> nettyClient.connectServer(), serverProperties.getReConnectTime(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("通道连接异常", cause);
        ctx.close();
    }
}
