package com.tuhu.socket.dto;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 文件信息
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
@Data
public class FileDto implements Serializable {

    /**
     * @Fields: serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 文件
     */
    private File file;

    /**
     * 文件唯一码
     */
    private String fileCode;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件总大小
     */
    private Long fileLength;

    /**
     * 块总数
     */
    private Integer blockTotal;

    /**
     * 类型， 0：传输文件 1：校验文件是否续传 2：合并文件
     */
    private Integer type;

    /**
     * 分块信息
     */
    private List<BlockDto> blockList;

}
