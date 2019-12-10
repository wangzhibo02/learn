package com.tuhu.socket.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 
 * @author xiongyan
 * @date 2019/12/06
 */
public class ServerThread implements Runnable {

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // 接收客户端信息
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024 * 1024];
            int len = 0;
            StringBuilder context = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                context.append(new String(bytes, 0, len, "UTF-8"));
            }
            System.out.println(context);

            // 返回客户端信息
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("成功".getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}