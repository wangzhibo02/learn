package com.tuhu.socket.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

/**
 * 服务端
 * 
 * @author xiongyan
 * @date 2019/12/06
 */
@Slf4j
public class Server {

    public static void main(String[] args) {
        int count = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(count * 10);
        try (ServerSocket server = new ServerSocket(8888)) {
            log.info("服务端启动成功，等待客户端连接！");
            while (true) {
                Socket socket = server.accept();
                log.info("客户端 {} 连接成功", socket.getLocalAddress().getHostName());
                try {
                    executorService.execute(() -> ServerHandler.doHandle(socket));
                } catch (Exception e) {
                    log.error("服务端处理失败", e);
                }
            }
        } catch (Exception e) {
            log.error("服务端失败", e);
        }
    }

}
