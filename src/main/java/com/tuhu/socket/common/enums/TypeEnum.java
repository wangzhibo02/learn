package com.tuhu.socket.common.enums;

import lombok.Getter;

/**
 * 操作类型枚举
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
@Getter
public enum TypeEnum {

    /**
     * 传输内容
     */
    SEND(0),

    /**
     * 校验
     */
    CHECK(1),

    /**
     * 合并
     */
    MERGE(2);

    /**
     * 类型
     */
    private int type;

    TypeEnum(int type) {
        this.type = type;
    }
}
