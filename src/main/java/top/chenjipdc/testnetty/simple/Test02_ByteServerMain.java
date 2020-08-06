package top.chenjipdc.testnetty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

/**
 * @author chenjipdc@gmail.com
 * @date 2020/8/3 2:18 下午
 *
 * 测试：curl http://127.0.0.1:10204
 */
public class Test02_ByteServerMain {
    public static void main(String[] args) {
        final int port = 10204;
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,
                worker);

        bootstrap.channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new ByteArrayDecoder(),
                                new ByteArrayEncoder(),
                                new SimpleChannelInboundHandler<Object>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        byte[] bytes = (byte[]) msg;
                                        System.out.println(new String(bytes));

                                        ctx.write(("服务器收到消息( " +  new String(bytes) + " )").getBytes());
                                    }
                                });
                    }
                });

        System.out.println("listening on port: " + port);
        try {

            ChannelFuture future = bootstrap.bind(port)
                    .sync();
            future.channel()
                    .closeFuture()
                    .sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
