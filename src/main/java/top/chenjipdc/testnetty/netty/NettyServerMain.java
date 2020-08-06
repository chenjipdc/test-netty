package top.chenjipdc.testnetty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import top.chenjipdc.testnetty.netty.codec.Test1ByteToMsgDecoder;
import top.chenjipdc.testnetty.netty.codec.Test1MsgToByteEncoder;

/**
 * @author chenjipdc@gmail.com
 * @date 2020-06-09 15:09
 */
public class NettyServerMain {

    public static void main(String[] args) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        //        testString(bootstrap,
        //                bossGroup,
        //                workerGroup);

        testCustomCodec(bootstrap,
                bossGroup,
                workerGroup);
    }

    /**
     * 测试自定义编码
     */
    private static void testCustomCodec(ServerBootstrap bootstrap, NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup) {
        bootstrap.group(bossGroup,
                workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new Test1ByteToMsgDecoder(),
                                new Test1MsgToByteEncoder(),
                                new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                        System.out.println("收到的请求内容：\n" + msg);

                                        String str = "HTTP/1.1 200\r\n" + "Content-Type: test/html\r\n" + "\r\n" +
                                                "这是http请求返回内容\r\n";
                                        ctx.writeAndFlush(str);
                                        ctx.channel().close();
                                    }
                                });
                    }
                });

        try {

            ChannelFuture future = bootstrap.bind(Consts.PORT)
                    .sync();
            future.channel()
                    .closeFuture()
                    .sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 测试字符串
     *
     * @param bootstrap
     * @param workerGroup
     */
    private static void testString(ServerBootstrap bootstrap, NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup) {
        bootstrap.group(bossGroup,
                workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new StringDecoder(),
                                new StringEncoder(),
                                new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println("server recv: " + msg);
                                        ctx.write(msg);
                                    }

                                    @Override
                                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                        ctx.flush();
                                    }

                                });
                    }
                });

        try {

            ChannelFuture future = bootstrap.bind(Consts.PORT)
                    .sync();
            future.channel()
                    .closeFuture()
                    .sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
