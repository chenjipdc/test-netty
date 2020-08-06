package top.chenjipdc.testnetty.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author chenjipdc@gmail.com
 * @date 2020/8/5 10:10 上午
 */
public class Test1MsgToByteEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        out.writeBytes(Unpooled.wrappedBuffer(msg.getBytes()));
    }
}
