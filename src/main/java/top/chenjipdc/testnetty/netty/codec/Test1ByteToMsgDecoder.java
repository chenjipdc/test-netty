package top.chenjipdc.testnetty.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author chenjipdc@gmail.com
 * @date 2020/8/5 10:11 上午
 */
public class Test1ByteToMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = ByteBufUtil.getBytes(in);
        String str = new String(bytes);
        out.add(str);
        in.skipBytes(in.readableBytes());
    }
}
