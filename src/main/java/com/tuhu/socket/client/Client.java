package com.tuhu.socket.client;

import java.io.File;

import com.tuhu.socket.common.constant.SocketConstant;
import com.tuhu.socket.common.util.FileUtil;
import com.tuhu.socket.dto.FileDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 客户端
 * 
 * @author xiongyan
 * @date 2019/12/06
 */
@Slf4j
public class Client {

    public static void main(String[] args) {
        // 文件
        File file = new File("d:/order-application.log");
        // 文件名称
        String fileName = file.getName();
        // 文件唯一码
        String fileCode = FileUtil.getFileCode(file);
        // 文件总大小
        long fileLength = file.length();
        FileDto fileDto = new FileDto();
        fileDto.setFile(file);
        fileDto.setFileCode(fileCode);
        fileDto.setFileName(fileName);
        fileDto.setFileLength(fileLength);

        try {
            if (fileLength <= SocketConstant.BLOCK_SIZE) {
                // 不分块传输
                CliendSend.sendFile(fileDto);
            } else {
                // 分块传输
                CliendSend.sendBlockFile(fileDto);
            }
        } catch (Exception e) {
            log.error("客户端传输文件失败", e);
        }
    }

}