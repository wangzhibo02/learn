package com.tuhu.socket.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.Socket;

import com.alibaba.fastjson.JSON;
import com.tuhu.socket.file.RequestContext;

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
            byte[] bytes = new byte[1024];
            int len = 0;
            StringBuilder content = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                content.append(new String(bytes, 0, len, "UTF-8"));
            }
            RequestContext requestContext = JSON.parseObject(content.toString(), RequestContext.class);
            if (requestContext.getIsBlock()) {
                // 分块

            } else {
                // 不分块

            }

            File file = new File("d:/xxx/" + requestContext.getFileCode() + "_" + requestContext.getBlockContextList().get(0).getNum() + ".log");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.append(requestContext.getBlockContextList().get(0).getContent());
            writer.close();
            inputStream.close();

            // 返回客户端信息
            // OutputStream outputStream = socket.getOutputStream();
            // outputStream.write("成功".getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}