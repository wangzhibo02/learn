package com.tuhu.socket.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * socket 响应信息
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
@Data
public class SocketResponse implements Serializable {

    /**
     * @Fields: serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 描述信息
     */
    private String msg;

    /**
     * 文件信息
     */
    private FileDto fileDto;
}
