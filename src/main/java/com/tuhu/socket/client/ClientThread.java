package com.tuhu.socket.client;

import com.alibaba.fastjson.JSON;
import com.tuhu.socket.file.RequestContext;

/**
 * 
 * @author xiongyan
 * @date 2019/12/06
 */
public class ClientThread implements Runnable {

    private RequestContext requestContext;

    public ClientThread(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public void run() {
        SocketContext context = new SocketContext();
        context.setHost("127.0.0.1");
        context.setPort(8888);
        context.setContent(JSON.toJSONString(requestContext));
        ClientSocket.send(context);
    }

}