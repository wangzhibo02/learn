package com.tuhu.socket.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import com.tuhu.socket.common.enums.ResponseCodeEnum;
import com.tuhu.socket.common.enums.TypeEnum;
import com.tuhu.socket.common.util.FileUtil;
import com.tuhu.socket.common.util.GzipUtil;
import com.tuhu.socket.common.util.ResponseUtil;
import com.tuhu.socket.common.util.SerializationUtil;
import com.tuhu.socket.dto.BlockDto;
import com.tuhu.socket.dto.FileDto;
import com.tuhu.socket.dto.SocketRequest;
import com.tuhu.socket.dto.SocketResponse;

/**
 * 客户端处理
 * 
 * @author xiongyan
 * @date 2019/12/09
 */
public class ClientHandler {

    /**
     * 处理
     * 
     * @param request
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
                List<BlockDto> blockList = fileDto.getBlockList();
                byte[] data = null;
                if (fileDto.getType() == TypeEnum.SEND.getType()) {
                    // 读取块信息
                    BlockDto blockDto = blockList.get(0);
                    data = FileUtil.getFileData(fileDto.getFile(), blockDto.getIndex(), blockDto.getSize());
                    data = GzipUtil.gZip(data);
                    blockDto.setSize((long) data.length);
                }
                // 块信息
                out.writeBytes(SerializationUtil.toStr(blockList));
                if (null != data) {
                    // 块内容
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
                    System.out.println(fileDto.getFileName() + " 文件传输完成");
                } else {
                    List<BlockDto> blockList = SerializationUtil.toBlockDto(blockStr);
                    fileDto.setBlockList(blockList);
                    if (fileDto.getType() == TypeEnum.CHECK.getType()) {
                        System.out.println("需要续传块信息：" + blockList.size());
                    } else if (fileDto.getType() == TypeEnum.MERGE.getType()) {
                        System.out.println(fileDto.getFileName() + "-文件合并完成 ");
                    } else {
                        System.out.println(fileDto.getFileName() + "-" + blockList.get(0).getNum() + "块传输完成 ");
                    }
                }
            }

            // 关闭资源
            reader.close();
            out.close();
            return response;
        } catch (Exception e) {
            System.out.println("客户端处理失败：" + e.getMessage());
            return ResponseUtil.error("客户端处理失败");
        }
    }

}
