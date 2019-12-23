package com.tuhu.socket.common.util;

import java.util.ArrayList;
import java.util.List;

import com.tuhu.socket.common.constant.SocketConstant;
import com.tuhu.socket.dto.BlockDto;
import com.tuhu.socket.dto.FileDto;

/**
 * 序列化工具类
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
public class SerializationUtil {

    /**
     * 文件信息转换字符串
     * 
     * @param fileDto
     * @return
     */
    public static String toStr(FileDto fileDto) {
        StringBuilder str = new StringBuilder();
        str.append(fileDto.getFileCode());
        str.append(SocketConstant.BLANK_SPACE);
        str.append(fileDto.getFileName());
        str.append(SocketConstant.BLANK_SPACE);
        str.append(fileDto.getFileLength());
        str.append(SocketConstant.BLANK_SPACE);
        str.append(fileDto.getBlockTotal());
        str.append(SocketConstant.BLANK_SPACE);
        str.append(fileDto.getType());
        str.append(SocketConstant.LINE_FEED);
        return str.toString();
    }

    /**
     * 块信息转换字符串
     * 
     * @param blockList
     * @return
     */
    public static String toStr(List<BlockDto> blockList) {
        StringBuilder str = new StringBuilder();
        for (BlockDto blockDto : blockList) {
            if (str.length() > 0) {
                str.append(SocketConstant.BLANK_SPACE);
            }
            str.append(blockDto.getNum());
            str.append(SocketConstant.UNDERLINE);
            str.append(blockDto.getIndex());
            str.append(SocketConstant.UNDERLINE);
            str.append(blockDto.getSize());
        }
        str.append(SocketConstant.LINE_FEED);
        return str.toString();
    }

    /**
     * 转文件信息
     * 
     * @param str
     * @return
     */
    public static FileDto toFileDto(String str) {
        String[] strs = str.split(SocketConstant.BLANK_SPACE);
        FileDto fileDto = new FileDto();
        fileDto.setFileCode(strs[0]);
        fileDto.setFileName(strs[1]);
        fileDto.setFileLength(Long.valueOf(strs[2]));
        fileDto.setBlockTotal(Integer.valueOf(strs[3]));
        fileDto.setType(Integer.valueOf(strs[4]));
        return fileDto;
    }

    /**
     * 转块信息
     * 
     * @param str
     * @return
     */
    public static List<BlockDto> toBlockDto(String str) {
        String[] strs = str.split(SocketConstant.BLANK_SPACE);
        List<BlockDto> blockList = new ArrayList<>(strs.length);
        for (String block : strs) {
            String[] blocks = block.split(SocketConstant.UNDERLINE);
            BlockDto blockDto = new BlockDto();
            blockDto.setNum(Integer.valueOf(blocks[0]));
            blockDto.setIndex(Long.valueOf(blocks[1]));
            blockDto.setSize(Long.valueOf(blocks[2]));
            blockList.add(blockDto);
        }
        return blockList;
    }

}
