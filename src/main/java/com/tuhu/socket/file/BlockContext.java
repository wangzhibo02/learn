package com.tuhu.socket.file;

import java.io.Serializable;

/**
 * 
 * @author xiongyan
 * @date 2019/12/09
 */
public class BlockContext implements Serializable {

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

    /**
     * 内容
     */
    private String content;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
