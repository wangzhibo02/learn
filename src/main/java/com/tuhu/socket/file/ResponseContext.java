package com.tuhu.socket.file;

import java.io.Serializable;

/**
 * 
 * @author xiongyan
 * @date 2019/12/09
 */
public class ResponseContext implements Serializable {

    /**
     * @Fields: serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 块编号
     */
    private Integer num;

    /**
     * 接收索引
     */
    private Long index;

    /**
     * 接收状态
     */
    private Boolean status;

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
