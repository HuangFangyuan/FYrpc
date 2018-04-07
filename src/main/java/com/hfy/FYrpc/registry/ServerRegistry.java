package com.hfy.FYrpc.registry;

import com.hfy.FYrpc.constant.Constants;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ServerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerRegistry.class);

    private CountDownLatch latch = new CountDownLatch(1);
    private String registryAddress;

    public ServerRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void register(String data) {
        ZooKeeper zk = connectServer();
        if (data != null) {
            createNode(zk, data);
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constants.ZK_SESSION_TIMEOUT, (watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            }));
        }
        catch (IOException e) {
            LOGGER.error("", e);
        }
        return zk;
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes();
            String path = zk.create(Constants.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        }
        catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }
}
