package com.tuhu.socket.client;

import java.io.File;

import com.tuhu.socket.common.constant.SocketConstant;
import com.tuhu.socket.common.util.FileUtil;
import com.tuhu.socket.dto.FileDto;
import com.tuhu.socket.dto.SocketRequest;

/**
 * 客户端
 * 
 * @author xiongyan
 * @date 2019/12/06
 */
public class Client {

    public static void main(String[] args) {
        if (null == args || args.length < 3) {
            System.out.println("客户端参数不能为空，例如：127.0.0.1 8888 d:/order-application.log");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String filePath = args[2];
        // 文件
        File file = new File(filePath);
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
            SocketRequest request = new SocketRequest();
            request.setHost(host);
            request.setPort(port);
            request.setFileDto(fileDto);
            if (fileLength <= SocketConstant.BLOCK_SIZE) {
                // 不分块传输
                CliendSend.sendFile(request);
            } else {
                // 分块传输
                CliendSend.sendBlockFile(request);
            }
        } catch (Exception e) {
            System.out.println("客户端传输文件失败：" + e.getMessage());
        }
    }

}