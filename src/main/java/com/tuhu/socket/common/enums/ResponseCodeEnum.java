package com.tuhu.socket.common.enums;

import lombok.Getter;

/**
 * 状态码枚举
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
@Getter
public enum ResponseCodeEnum {

    /**
     * 成功
     */
    SUCCESS(1),

    /**
     * 失败
     */
    ERROR(0);

    /**
     * 状态码
     */
    private int code;

    ResponseCodeEnum(int code) {
        this.code = code;
    }
}
