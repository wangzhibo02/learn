package com.tuhu.socket.common.util;

import com.tuhu.socket.common.enums.ResponseCodeEnum;
import com.tuhu.socket.dto.FileDto;
import com.tuhu.socket.dto.SocketResponse;

/**
 * ResponseUtil
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
public class ResponseUtil {

    /**
     * 成功
     * 
     * @param fileDto
     * @return
     */
    public static SocketResponse success(FileDto fileDto) {
        SocketResponse response = new SocketResponse();
        response.setCode(ResponseCodeEnum.SUCCESS.getCode());
        response.setFileDto(fileDto);
        return response;
    }

    /**
     * 失败
     * 
     * @param msg
     * @return
     */
    public static SocketResponse error(String msg) {
        SocketResponse response = new SocketResponse();
        response.setCode(ResponseCodeEnum.ERROR.getCode());
        response.setMsg(msg);
        return response;
    }
}
