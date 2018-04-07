package com.hfy.FYrpc.registry;

import com.hfy.FYrpc.anno.RpcService;
import com.hfy.FYrpc.bean.RpcRequest;
import com.hfy.FYrpc.codec.RpcDecoder;
import com.hfy.FYrpc.codec.RpcEncoder;
import com.hfy.FYrpc.handler.RpcHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private String serverAddress;
    private ServerRegistry serverRegistry;

    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcServer(String serverAddress, ServerRegistry serverRegistry) {
        this.serverAddress = serverAddress;
        this.serverRegistry = serverRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serverBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (serverBeanMap.size() > 0) {
            for (Object serverBean: serverBeanMap.values()) {
                String interfaceName = serverBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, serverBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new RpcDecoder(RpcRequest.class))
                                .addLast(new RpcEncoder())
                                .addLast(new RpcHandler(handlerMap));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            String[] splits = serverAddress.split(":");
            String ip = splits[0];
            int port = Integer.parseInt(splits[1]);
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            LOGGER.debug("rpc server start on port {}", port);
            serverRegistry.register(ip);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e){
            LOGGER.error("interrupted:{}", e);
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
