package cn.edcheung.springskills.io.nettyapp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverChannel;

    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定监听端口
     *
     * @param port 监听端口号
     */
    public MultiplexerTimeServer(int port) {
        try {
            // 步骤一：打开ServerSocketChannel,用于监听客户端的连接，它是所有客户端连接的父管道
            serverChannel = ServerSocketChannel.open();
            // 步骤二：绑定监听端口，设置连接为非阻塞模式
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverChannel.configureBlocking(false);
            // 步骤三：创建多路复用器，对Channel和TCP参数进行配置
            selector = Selector.open();
            // 步骤四：将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听ACCEPT事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                // 步骤五：多路复用器在线程run方法的无限循环体内轮询准备就绪的Key
                // 无论读写等事件是否发生，selector每隔1s都被唤醒一次
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        // 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册去关闭，所以不需要重复去释放资源
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 处理新接入的请求消息
            if (key.isAcceptable()) {
                // Accept the new connection
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                // 步骤六：多路复用器监听到有新的客户端接入，处理新的接入请求，完成TCP三次握手，建立物理链路
                SocketChannel sc = ssc.accept();
                // 步骤七：设置客户端链路为非阻塞模式
                sc.configureBlocking(false);
                // 步骤八：将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读操作，读取客户端发送的网络消息
                // Add the new connection to the selector
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                // Read te data
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                // 步骤九：异步读取客户端请求消息到缓冲区
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
//                    Object message = null;
//                    while (buffer.hasRemain()) {
//                        byteBuffer.mark();
//                        Object message = decode(byteBuffer);
//                        if (message == null) {
//                            byteBuffer.reset();
//                            break;
//                        }
//                        messageList.add(message);
//                    }
//                    if (!byteBuffer.hasRemain()) {
//                        byteBuffer.clear();
//                    } else {
//                        byteBuffer.compact();
//                    }
//                    if (messageList != null && !messageList.isEmpty()) {
//                        for (object messageE : messageList) {
//                            handlerTask(messageE);
//                        }
//                    }
                    // 读取到字节，对字节进行编解码
                    readBuffer.flip();
                    // 步骤十：对ByteBuffer进行编解码，如果有半包消息指针reset，继续读取后续的报文，
                    // 将解码成功的消息封装成Task，投递到业务线程池中，进行业务逻辑编排
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("The time server receive order : " + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "Bad ORDER";
                    // 步骤十一：将POJO对象encode成ByteBuffer,调用SocketChannel的异步write接口，将消息异步发送给客户端
                    doWrite(sc, currentTime);
                } else if (readBytes < 0) {
                    // 对端链路关闭，关闭SocketChannel，释放资源
                    key.cancel();
                    sc.close();
                } else {
                    // 没有读取到字节，属于正常场景，忽略
                }
            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            // 注意：如果发送区TCP缓冲区满，会导致写半包，此时，需要注册监听写操作位，循环写，直到整包消息写入TCP缓冲区。
            channel.write(writeBuffer);
        }
    }
}
