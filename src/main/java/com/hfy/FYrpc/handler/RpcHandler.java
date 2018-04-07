package com.hfy.FYrpc.handler;

import com.hfy.FYrpc.bean.RpcRequest;
import com.hfy.FYrpc.bean.RpcResponse;
import com.hfy.FYrpc.utils.InvocationUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);

    private Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Exception e) {
            LOGGER.error("error happened {}", e);
            response.setError(e);
        }
        channelHandlerContext.writeAndFlush(response);
    }

    private Object handle(RpcRequest request) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String interfaceName = request.getInterfaceName();
        Object serviceBean = handlerMap.get(interfaceName);
        return InvocationUtil.invoke(serviceBean, request.getMethodName(), request.getParameterTypes(), request.getParameters());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("error happened {}", cause);
    }
}
