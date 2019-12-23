package com.tuhu.socket.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import com.tuhu.socket.common.enums.ResponseCodeEnum;
import com.tuhu.socket.common.enums.TypeEnum;
import com.tuhu.socket.common.util.FileUtil;
import com.tuhu.socket.common.util.ResponseUtil;
import com.tuhu.socket.common.util.SerializationUtil;
import com.tuhu.socket.dto.BlockDto;
import com.tuhu.socket.dto.FileDto;
import com.tuhu.socket.dto.SocketRequest;
import com.tuhu.socket.dto.SocketResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 客户端处理
 * 
 * @author xiongyan
 * @date 2019/12/09
 */
@Slf4j
public class ClientHandler {

    /**
     * 处理
     * 
     * @param context
     * @return
     */
    public static SocketResponse doHandle(SocketRequest request) {
        try (Socket socket = new Socket(request.getHost(), request.getPort());) {
            FileDto fileDto = request.getFileDto();
            // 发送信息给服务器端
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            // 文件信息
            out.writeBytes(SerializationUtil.toStr(fileDto));
            if (fileDto.getType() != TypeEnum.MERGE.getType()) {
                // 块信息
                out.writeBytes(SerializationUtil.toStr(fileDto.getBlockList()));
                if (fileDto.getType() == TypeEnum.SEND.getType()) {
                    // 块内容
                    BlockDto blockDto = fileDto.getBlockList().get(0);
                    byte[] data = FileUtil.getFileData(fileDto.getFile(), blockDto.getIndex(), blockDto.getSize());
                    out.write(data);
                }
            }
            out.flush();
            socket.shutdownOutput();

            // 获取服务器响应信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            SocketResponse response = new SocketResponse();
            // 状态码
            response.setCode(Integer.parseInt(reader.readLine()));
            if (response.getCode() == ResponseCodeEnum.SUCCESS.getCode()) {
                // 文件信息
                fileDto = SerializationUtil.toFileDto(reader.readLine());
                response.setFileDto(fileDto);
                // 块信息
                String blockStr = reader.readLine();
                if (null == blockStr) {
                    log.info("{} 文件传输完成", fileDto.getFileName());
                } else {
                    List<BlockDto> blockList = SerializationUtil.toBlockDto(blockStr);
                    fileDto.setBlockList(blockList);
                    if (fileDto.getType() == TypeEnum.CHECK.getType()) {
                        log.info("需要续传块信息：{}", blockList);
                    } else if (fileDto.getType() == TypeEnum.MERGE.getType()) {
                        log.info("{}-文件合并完成 ", fileDto.getFileName());
                    } else {
                        log.info("{}-{}块传输完成 ", fileDto.getFileName(), blockList.get(0).getNum());
                    }
                }
            }

            // 关闭资源
            reader.close();
            out.close();
            return response;
        } catch (Exception e) {
            log.error("客户端处理失败", e);
            return ResponseUtil.error("客户端处理失败");
        }
    }

}
