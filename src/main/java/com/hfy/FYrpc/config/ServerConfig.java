package com.hfy.FYrpc.config;

import com.hfy.FYrpc.registry.RpcServer;
import com.hfy.FYrpc.registry.ServerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Bean
    public ServerRegistry serverRegistry(@Value("${registry.address}")String registryAddress) {
        return new ServerRegistry(registryAddress);
    }

    @Bean
    public RpcServer rpcServer(@Value("${registry.address}")String serverAddress, ServerRegistry serverRegistry) {
        return new RpcServer(serverAddress, serverRegistry);
    }
}
