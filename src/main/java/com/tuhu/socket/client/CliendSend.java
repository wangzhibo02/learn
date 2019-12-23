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

import lombok.extern.slf4j.Slf4j;

/**
 * 客户端发送
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
@Slf4j
public class CliendSend {

    /**
     * 完整发送
     * 
     * @param fileDto
     * @throws Exception
     */
    public static void sendFile(FileDto fileDto) throws Exception {
        fileDto.setBlockTotal(1);
        fileDto.setType(TypeEnum.SEND.getType());
        BlockDto blockDto = new BlockDto();
        blockDto.setNum(1);
        blockDto.setIndex(0L);
        blockDto.setSize(fileDto.getFileLength());
        fileDto.setBlockList(Arrays.asList(blockDto));

        SocketRequest request = new SocketRequest();
        request.setHost("127.0.0.1");
        request.setPort(8888);
        request.setFileDto(fileDto);
        long startTime = System.currentTimeMillis();
        SocketResponse response = ClientHandler.doHandle(request);
        if (null != response && response.getCode() == ResponseCodeEnum.SUCCESS.getCode()) {
            log.info("文件传输成功，用时[" + (System.currentTimeMillis() - startTime) + "]毫秒");
        } else {
            log.error("文件传输失败：{}", response.getMsg());
        }
    }

    /**
     * 分块传输
     * 
     * @param fileDto
     * @throws Exception
     */
    public static void sendBlockFile(FileDto fileDto) throws Exception {
        long startTime = System.currentTimeMillis();
        // 校验文件
        List<BlockDto> blockList = checkFile(fileDto);
        if (null == blockList) {
            log.info("文件校验成功，用时[" + (System.currentTimeMillis() - startTime) + "]毫秒");
        } else {
            // 分块发送
            sendBlock(fileDto, blockList);
            // 合并文件
            mergeFile(fileDto);
            log.info("分块传输成功，用时[" + (System.currentTimeMillis() - startTime) + "]毫秒");
        }
    }

    /**
     * 校验文件块信息，主要用于续传
     * 
     * @param fileDto
     * @return
     */
    private static List<BlockDto> checkFile(FileDto fileDto) {
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

        SocketRequest request = new SocketRequest();
        request.setHost("127.0.0.1");
        request.setPort(8888);
        request.setFileDto(fileDto);
        SocketResponse response = ClientHandler.doHandle(request);
        if (null == response || response.getCode() == ResponseCodeEnum.ERROR.getCode()) {
            log.error("文件校验失败：{}", response.getMsg());
            return null;
        }
        return response.getFileDto().getBlockList();
    }

    /**
     * 块信息传输
     * 
     * @param sourceFileDto
     * @param blockList
     * @throws Exception
     */
    private static void sendBlock(FileDto sourceFileDto, List<BlockDto> blockList) throws Exception {
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

            SocketRequest request = new SocketRequest();
            request.setHost("127.0.0.1");
            request.setPort(8888);
            request.setFileDto(fileDto);
            executorService.execute(() -> ClientHandler.doHandle(request));
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    /**
     * 文件合并
     * 
     * @param fileDto
     */
    private static void mergeFile(FileDto fileDto) {
        fileDto.setType(TypeEnum.MERGE.getType());
        SocketRequest request = new SocketRequest();
        request.setHost("127.0.0.1");
        request.setPort(8888);
        request.setFileDto(fileDto);
        ClientHandler.doHandle(request);
    }
}
