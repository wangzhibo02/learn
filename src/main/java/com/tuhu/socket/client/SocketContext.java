package com.tuhu.socket.client;

import java.io.Serializable;

import com.tuhu.socket.file.RequestContext;

/**
 * 
 * @author xiongyan
 * @date 2019/12/09
 */
public class SocketContext implements Serializable {

    /**
     * @Fields: serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * IP
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 文件内容
     */
    private RequestContext requestContext;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

}
