package com.tuhu.socket.client;

import java.io.OutputStream;
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
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(context.getContent().getBytes("UTF-8"));
            outputStream.close();

            // 接收服务器端信息
            /*InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024 * 1024];
            int len = 0;
            StringBuilder content = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                content.append(new String(bytes, 0, len, "UTF-8"));
            }
            inputStream.close();
            return content.toString();*/
            return "成功";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
