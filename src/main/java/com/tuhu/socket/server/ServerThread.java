package com.tuhu.socket.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String content = getContent(reader);
            reader.close();
            RequestContext requestContext = JSON.parseObject(content, RequestContext.class);
            // 写入文件
            writerFile(requestContext);

            // 返回客户端信息
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            pw.write("成功");
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getContent(BufferedReader reader) throws Exception {
        StringBuilder content = new StringBuilder();
        while (true) {
            String data = reader.readLine();
            if (null == data) {
                break;
            }
            content.append(data);
        }
        return content.toString();
    }

    private void writerFile(RequestContext requestContext) throws Exception {
        File file = new File("d:/xxx/" + requestContext.getFileCode() + "_" + requestContext.getBlockContextList().get(0).getNum() + ".log");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        writer.append(requestContext.getBlockContextList().get(0).getContent());
        writer.close();
    }
}