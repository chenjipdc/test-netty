package top.chenjipdc.testnetty.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import top.chenjipdc.testnetty.netty.codec.Test1ByteToMsgDecoder;
import top.chenjipdc.testnetty.netty.codec.Test1MsgToByteEncoder;

/**
 * @author chenjipdc@gmail.com
 * @date 2020-06-09 15:09
 */
public class NettyClientMain {

    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        //        testString(bootstrap,
        //                workerGroup);

        testCustomCodec(bootstrap,
                workerGroup);
    }

    /**
     * 测试自定义编码
     */
    private static void testCustomCodec(Bootstrap bootstrap, NioEventLoopGroup workerGroup) {
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new Test1ByteToMsgDecoder(),
                                new Test1MsgToByteEncoder(),
                                new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        ctx.writeAndFlush("我发送了一条消息");
                                    }

                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                    }
                });

        try {
            ChannelFuture future = bootstrap.connect(Consts.HOST,
                    Consts.PORT)
                    .sync();
            future.channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 测试字符串
     *
     * @param bootstrap
     * @param workerGroup
     */
    private static void testString(Bootstrap bootstrap, NioEventLoopGroup workerGroup) {
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new StringEncoder(),
                                new StringDecoder(),
                                new ChannelInboundHandlerAdapter() {

                                    int i = 0;

                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        ctx.writeAndFlush("send " + i++);
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println("client recv: " + msg);
                                        if (i < 100) {
                                            ctx.write("send " + i++);
                                        }
                                    }

                                    @Override
                                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                        ctx.flush();
                                    }


                                });
                    }
                });

        try {
            ChannelFuture future = bootstrap.connect(Consts.HOST,
                    Consts.PORT)
                    .sync();
            future.channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
