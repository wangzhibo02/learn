package com.tuhu.socket.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 
 * @author xiongyan
 * @date 2019/12/09
 */
public class ClientSocket {

    public static String send(SocketContext context) {
        try (Socket socket = new Socket(context.getHost(), context.getPort())) {
            // 发送信息给服务器端
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.write(context.getContent());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String content = getContent(reader);
            reader.close();
            writer.close();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getContent(BufferedReader reader) throws Exception {
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
}
