package top.chenjipdc.testnetty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author chenjipdc@gmail.com
 * @date 2020-06-09 10:52
 */
@SuppressWarnings("AlibabaAvoidManuallyCreateThread")
public class NioUtils {

    private static final int PORT = 29901;

    public static void startServer() {

        Selector selector;
        ServerSocketChannel socketChannel;
        try {
            selector = Selector.open();
            socketChannel = ServerSocketChannel.open();
            // 异步处理
            socketChannel.configureBlocking(false);

            socketChannel.bind(new InetSocketAddress(PORT),
                    1024);

            socketChannel.register(selector,
                    SelectionKey.OP_ACCEPT);

            final Selector sel = selector;
            new Thread(() -> {
                while (true) {
                    try {
                        // 阻塞，等待事件
                        sel.select(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Set<SelectionKey> selectionKeys = sel.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    SelectionKey key;
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        iterator.remove();

                        if (key.isValid()) {
                            // 注册读事件
                            if (key.isAcceptable()) {
                                System.out.println("注册读事件");
                                try {
                                    ServerSocketChannel sChannel = (ServerSocketChannel) key.channel();
                                    SocketChannel accept = sChannel.accept();
                                    accept.configureBlocking(false);
                                    accept.register(sel,
                                            SelectionKey.OP_READ);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (key.isReadable()) {
                                // 读请求
                                System.out.println("接收读事件");
                                SocketChannel channel = (SocketChannel) key.channel();
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                try {
                                    int read = channel.read(byteBuffer);
                                    if (read > 0) {

                                        byteBuffer.flip();
                                        //根据缓冲区可读字节数创建字节数组
                                        byte[] bytes = new byte[byteBuffer.remaining()];
                                        //将缓冲区可读字节数组复制到新建的数组中
                                        byteBuffer.get(bytes);

                                        System.out.println("server recv: " + new String(bytes));

                                        byte[] writeBytes = ("nio write => " + new String(bytes)).getBytes();

                                        ByteBuffer w = ByteBuffer.allocate(writeBytes.length);
                                        w.put(writeBytes);
                                        w.flip();
                                        channel.write(w);

                                    } else {
                                        channel.close();
                                        key.cancel();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void startSyncClient(int id) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1",
                    PORT));


            byte[] writeBytes = ("nio write => " + id).getBytes();

            ByteBuffer w = ByteBuffer.allocate(writeBytes.length);
            w.put(writeBytes);
            w.flip();
            socketChannel.write(w);

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int read = socketChannel.read(byteBuffer);
            if (read > 0) {
                byteBuffer.flip();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                System.out.println("client recv: " + new String(bytes));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
