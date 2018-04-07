package com.hfy.FYrpc.codec;

import com.hfy.FYrpc.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    public RpcDecoder() {
    }

    public RpcDecoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int len = 0;
        if (byteBuf.readableBytes() >= 4){
            len = byteBuf.readInt();
        }
        byte[] bytes = new byte[len];
        if (byteBuf.readableBytes() >= len) {
            byteBuf.readBytes(bytes);
        }
        list.add(SerializationUtil.deserialize(bytes, clazz));
    }
}
