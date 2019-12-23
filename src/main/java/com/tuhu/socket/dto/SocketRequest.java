package com.tuhu.socket.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * socket 请求信息
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
@Data
public class SocketRequest implements Serializable {

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
     * 文件信息
     */
    private FileDto fileDto;
}
