package com.tuhu.socket.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.tuhu.socket.common.constant.SocketConstant;
import com.tuhu.socket.common.enums.ResponseCodeEnum;
import com.tuhu.socket.common.enums.TypeEnum;
import com.tuhu.socket.common.util.FileUtil;
import com.tuhu.socket.common.util.SerializationUtil;
import com.tuhu.socket.dto.BlockDto;
import com.tuhu.socket.dto.FileDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 服务端处理
 * 
 * @author xiongyan
 * @date 2019/12/06
 */
@Slf4j
public class ServerHandler {

    /**
     * 处理
     * 
     * @param socket
     */
    public static void doHandle(Socket socket) {
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            in = new DataInputStream(socket.getInputStream());
            FileDto fileDto = SerializationUtil.toFileDto(in.readLine());
            if (fileDto.getType() == TypeEnum.MERGE.getType()) {
                // 合并文件
                FileUtil.mergeFile(fileDto);
            } else {
                List<BlockDto> blockList = SerializationUtil.toBlockDto(in.readLine());
                fileDto.setBlockList(blockList);
                if (fileDto.getType() == TypeEnum.CHECK.getType()) {
                    // 检查文件是否续传
                    FileUtil.checkFile(fileDto);
                } else {
                    long size = blockList.get(0).getSize();
                    byte[] data = new byte[(int) size];
                    in.readFully(data);
                    // 写入文件
                    FileUtil.writerFile(fileDto, data);
                }
            }
            socket.shutdownInput();

            // 返回客户端信息
            out = new DataOutputStream(socket.getOutputStream());
            out.writeBytes(ResponseCodeEnum.SUCCESS.getCode() + SocketConstant.LINE_FEED);
            out.writeBytes(SerializationUtil.toStr(fileDto));
            if (null != fileDto.getBlockList() && fileDto.getBlockList().size() > 0) {
                out.writeBytes(SerializationUtil.toStr(fileDto.getBlockList()));
            }
            out.flush();
        } catch (Exception e) {
            log.error("服务端处理失败", e);
            if (null == out) {
                try {
                    out = new DataOutputStream(socket.getOutputStream());
                    out.writeBytes(ResponseCodeEnum.ERROR.getCode() + SocketConstant.LINE_FEED);
                    out.flush();
                } catch (IOException e1) {
                }
            }
        } finally {
            // 关闭资源
            try {
                if (null != out) {
                    out.close();
                }
                if (null != in) {
                    in.close();
                }
                socket.close();
            } catch (Exception e2) {
            }
        }
    }

}