package com.tuhu.socket.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.tuhu.socket.common.constant.SocketConstant;
import com.tuhu.socket.common.enums.ResponseCodeEnum;
import com.tuhu.socket.common.enums.TypeEnum;
import com.tuhu.socket.dto.BlockDto;
import com.tuhu.socket.dto.FileDto;
import com.tuhu.socket.dto.SocketRequest;
import com.tuhu.socket.dto.SocketResponse;

/**
 * 客户端发送
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
public class CliendSend {

    /**
     * 完整发送
     * 
     * @param request
     * @throws Exception
     */
    public static void sendFile(SocketRequest request) throws Exception {
        FileDto fileDto = request.getFileDto();
        fileDto.setBlockTotal(1);
        fileDto.setType(TypeEnum.SEND.getType());
        BlockDto blockDto = new BlockDto();
        blockDto.setNum(1);
        blockDto.setIndex(0L);
        blockDto.setSize(fileDto.getFileLength());
        fileDto.setBlockList(Arrays.asList(blockDto));

        long startTime = System.currentTimeMillis();
        SocketResponse response = ClientHandler.doHandle(request);
        if (null != response && response.getCode() == ResponseCodeEnum.SUCCESS.getCode()) {
            System.out.println("文件传输成功，用时[" + (System.currentTimeMillis() - startTime) + "]毫秒");
        } else {
            System.out.println("文件传输失败：" + response.getMsg());
        }
    }

    /**
     * 分块传输
     * 
     * @param request
     * @throws Exception
     */
    public static void sendBlockFile(SocketRequest request) throws Exception {
        long startTime = System.currentTimeMillis();
        // 校验文件
        List<BlockDto> blockList = checkFile(request);
        if (null == blockList) {
            System.out.println("文件校验成功，用时[" + (System.currentTimeMillis() - startTime) + "]毫秒");
        } else {
            // 分块发送
            sendBlock(request, blockList);
            System.out.println("分块传输成功，用时[" + (System.currentTimeMillis() - startTime) + "]毫秒");
            // 合并文件
            mergeFile(request);
            // System.out.println("文件合并成功，用时[" + (System.currentTimeMillis() - startTime) + "]毫秒");
        }
    }

    /**
     * 校验文件块信息，主要用于续传
     * 
     * @param request
     * @return
     */
    private static List<BlockDto> checkFile(SocketRequest request) {
        FileDto fileDto = request.getFileDto();
        int count = (int) (fileDto.getFileLength() / SocketConstant.BLOCK_SIZE);
        if (fileDto.getFileLength() % SocketConstant.BLOCK_SIZE > 0) {
            count++;
        }
        fileDto.setBlockTotal(count);
        fileDto.setType(TypeEnum.CHECK.getType());
        List<BlockDto> blockList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            BlockDto blockDto = new BlockDto();
            blockDto.setNum(i + 1);
            blockDto.setIndex(SocketConstant.BLOCK_SIZE * i);
            if (fileDto.getFileLength() < blockDto.getIndex() + SocketConstant.BLOCK_SIZE) {
                blockDto.setSize(fileDto.getFileLength() - blockDto.getIndex());
            } else {
                blockDto.setSize(SocketConstant.BLOCK_SIZE);
            }
            blockList.add(blockDto);
        }
        fileDto.setBlockList(blockList);

        SocketResponse response = ClientHandler.doHandle(request);
        if (null == response || response.getCode() == ResponseCodeEnum.ERROR.getCode()) {
            System.out.println("文件校验失败：" + response.getMsg());
            return null;
        }
        return response.getFileDto().getBlockList();
    }

    /**
     * 块信息传输
     * 
     * @param request
     * @param blockList
     * @throws Exception
     */
    private static void sendBlock(SocketRequest request, List<BlockDto> blockList) throws Exception {
        FileDto sourceFileDto = request.getFileDto();

        ExecutorService executorService = Executors.newFixedThreadPool(blockList.size() > 20 ? 20 : blockList.size());
        for (BlockDto blockDto : blockList) {
            FileDto fileDto = new FileDto();
            fileDto.setFile(sourceFileDto.getFile());
            fileDto.setFileCode(sourceFileDto.getFileCode());
            fileDto.setFileName(sourceFileDto.getFileName());
            fileDto.setFileLength(sourceFileDto.getFileLength());
            fileDto.setBlockTotal(blockList.size());
            fileDto.setType(TypeEnum.SEND.getType());
            fileDto.setBlockList(Arrays.asList(blockDto));

            SocketRequest socketRequest = new SocketRequest();
            socketRequest.setHost(request.getHost());
            socketRequest.setPort(request.getPort());
            socketRequest.setFileDto(fileDto);
            executorService.execute(() -> ClientHandler.doHandle(socketRequest));
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    /**
     * 文件合并
     * 
     * @param request
     */
    private static void mergeFile(SocketRequest request) {
        FileDto fileDto = request.getFileDto();
        fileDto.setType(TypeEnum.MERGE.getType());
        new Thread(() -> ClientHandler.doHandle(request)).start();
    }
}
