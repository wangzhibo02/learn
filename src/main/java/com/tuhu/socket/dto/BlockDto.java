package com.tuhu.socket.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 块信息
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
@Data
public class BlockDto implements Serializable {

    /**
     * @Fields: serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 块编号
     */
    private Integer num;

    /**
     * 块索引
     */
    private Long index;

    /**
     * 块大小
     */
    private Long size;

}
