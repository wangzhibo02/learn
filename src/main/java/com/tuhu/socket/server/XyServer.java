package com.tuhu.socket.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author xiongyan
 * @date 2019/12/06
 */
public class XyServer {

    public static void main(String[] args) {
        int count = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(count * 10);
        try (ServerSocket server = new ServerSocket(8888)) {
            System.out.println("服务端启动成功，等待客户端连接！");
            while (true) {
                Socket socket = server.accept();
                System.out.println("客户端" + socket.getLocalAddress().getHostName() + "连接成功");
                executorService.execute(new ServerThread(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
