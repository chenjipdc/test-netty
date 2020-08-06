package top.chenjipdc.testnetty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.ExecutionException;

/**
 * @author chenjipdc@gmail.com
 * @date 2020/8/3 11:51 上午
 * <p>
 * 测试：curl http://127.0.0.1:10203
 */
public class Test01_StringServerMain {
    public static void main(String[] args) throws InterruptedException {
        final int port = 10203;
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

                        pipeline.addLast(new StringDecoder(),
                                new StringEncoder(),
                                new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                        System.out.println(msg);

                                        ctx.write("服务端收到的消息( " + msg + " )");
                                    }
                                });
                    }
                });

        System.out.println("listening on port: " + port);
        try {

            ChannelFuture sync = bootstrap.bind(port)
                    .sync();
            int a = 0;
            while (true) {
                Thread.sleep(1000);
                a++;
                System.out.println(a);
                if (a == 20){
                    sync.channel().close();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
