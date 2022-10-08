package cn.edcheung.springskills.io.nettyapp.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        // 既然已经接收客户端成功了，为什么还要再次调用accept方法呢？
        // 原因是这样的：调用AsynchronousServerSocketChannel的accept方法后，
        // 如果有新的客户端连接接入，系统将回调我们传入的CompletionHandler实例的completed方法，表示新的客户端已经接入成功。
        // 因为一个AsynchronousServerSocket Channel可以接收成千上万个客户端，
        // 所以需要继续调用它的accept方法，接收其他的客户端连接，最终形成一个循环。
        // 每当接收一个客户读连接成功之后，再异步接收新的客户端连接。
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // ByteBuffer dst：接收缓冲区，用于从异步Channel中读取数据包：
        // A attachment：异步Channel携带的附件，通知回调的时候作为入参使用；
        // CompletionHandler<Integer,? super A>：接收通知回调的业务Handler。
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
