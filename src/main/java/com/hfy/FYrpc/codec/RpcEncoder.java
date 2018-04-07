package com.hfy.FYrpc.codec;

import com.hfy.FYrpc.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder<RpcResponse> extends MessageToByteEncoder<RpcResponse> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse, ByteBuf byteBuf) throws Exception {
        byte[] data = SerializationUtil.serialize(rpcResponse);
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
        channelHandlerContext.flush();
    }
}
