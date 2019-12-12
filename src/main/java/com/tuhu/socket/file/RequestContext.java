package com.tuhu.socket.file;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author xiongyan
 * @date 2019/12/09
 */
public class RequestContext implements Serializable {

    /**
     * @Fields: serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 是否分块
     */
    private Boolean isBlock;

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
    private Long length;

    /**
     * 块信息
     */
    private List<BlockContext> blockContextList;

    public Boolean getIsBlock() {
        return isBlock;
    }

    public void setIsBlock(Boolean isBlock) {
        this.isBlock = isBlock;
    }

    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public List<BlockContext> getBlockContextList() {
        return blockContextList;
    }

    public void setBlockContextList(List<BlockContext> blockContextList) {
        this.blockContextList = blockContextList;
    }

    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();
        content.append(isBlock);
        content.append("_");
        content.append(fileCode);
        content.append("_");
        content.append(fileName);
        content.append("_");
        content.append(length);
        content.append("_");
        for (int i = 0; i < blockContextList.size(); i++) {
            BlockContext blockContext = blockContextList.get(i);
            if (i > 0) {
                content.append("|");
            }
            content.append(blockContext.getNum());
            content.append("-");
            content.append(blockContext.getIndex());
            content.append("-");
            content.append(blockContext.getSize());
            if (null != blockContext.getContent() && !"".equals(blockContext.getContent())) {
                content.append("-");
                content.append(blockContext.getContent());
            }
        }
        return content.toString();
    }

}
