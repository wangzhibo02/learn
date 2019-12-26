package com.tuhu.socket.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务端
 * 
 * @author xiongyan
 * @date 2019/12/06
 */
public class Server {

    public static void main(String[] args) {
        if (null == args || args.length == 0) {
            System.out.println("端口号不能为空，例如：8888");
            return;
        }
        int port = Integer.parseInt(args[0]);
        int count = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(count * 10);
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("服务端启动成功，等待客户端连接！");
            while (true) {
                Socket socket = server.accept();
                System.out.println("客户端 " + socket.getLocalAddress().getHostName() + " 连接成功");
                try {
                    executorService.execute(() -> ServerHandler.doHandle(socket));
                } catch (Exception e) {
                    System.out.println("服务端处理失败：" + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("服务端失败：" + e.getMessage());
        }
    }

}
