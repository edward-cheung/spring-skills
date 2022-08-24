package cn.edcheung.springskills.io.nettyapp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandler implements Runnable {

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimeClientHandler(String host, int port) {
        this.host = host == null ? "127.0.0.1" : host;
        this.port = port;
        try {
            // 步骤六：创建Reactor线程，创建多路复用器并启动线程
            selector = Selector.open();
            // 步骤一：打开SocketChannel,绑定客户端本地地址（可选，默认系统会随机分配一个可用的本地地址)
            socketChannel = SocketChannel.open();
            // 步骤二：设置SocketChannel为非阻塞模式，同时设置客户端连接的TCP参数
            socketChannel.configureBlocking(false);
//            socket.setReuseAddress(true);
//            socket.setReceiveBufferSize(BUFFER_SIZE);
//            socket.setSendBufferSize(BUFFER_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // 步骤七：多路复用器在线程run方法的无限循环体内轮询准备就绪的Key
        while (!stop) {
            try {
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

    private void doConnect() throws IOException {
        // 步骤三：异步连接服务端
        // 步骤四：判断是否连接成功，如果连接成功，则直接注册读状态位到多路复用器中，
        // 如果当前没有连接成功（异步连接，返回false,说明客户端已经发送sync包，服务端没有返回ack包，物理链路还没有建立)
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            // 步骤五：向Reactor线程的多路复用器注册OPCONNECT状态位，监听服务端的TCP ACK应答
            // 如果直接连接成功，则注册到多路复用器，发送请求消息，读应答
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel sc) throws IOException {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        sc.write(writeBuffer);
        if (writeBuffer.hasRemaining()) {
            System.out.println("Send order 2 server succeed,");
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 判断是否连接成功
            SocketChannel sc = (SocketChannel) key.channel();
            // 步骤八：接收connect事件进行处理
            if (key.isConnectable()) {
                // 步骤九：判断连接结果，如果连接成功，注册读事件到多路复用器
                if (sc.finishConnect()) {
                    // 步骤十：注册读事件到多路复用器
                    sc.register(selector, SelectionKey.OP_READ);
                    // 步骤十三：将POJO对象encode成ByteBuffer,调用SocketChannel的异步write接口， 将消息异步发送给客户端
                    doWrite(sc);
                } else {
                    // 连接失败，进程退出
                    System.exit(1);
                }
            }
            if (key.isReadable()) {
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                // 步骤十一：异步读客户端请求消息到缓冲区
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
                    // 步骤十二：对ByteBuffer进行编解码，如果有半包消息接收缓冲区Reset,继续读取后续的报文，
                    // 将解码成功的消息封装成Tsk,投递到业务线程池中，进行业务逻辑编排
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("Now is : " + body);
                    this.stop = true;
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
}
