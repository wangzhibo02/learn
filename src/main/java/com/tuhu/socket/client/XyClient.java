package com.tuhu.socket.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tuhu.socket.file.BlockContext;
import com.tuhu.socket.file.RequestContext;

/**
 * 
 * @author xiongyan
 * @date 2019/12/06
 */
public class XyClient {

    // 块大小10M
    public static final long BLOCK_SIZE = 1024 * 1024 * 10;

    public static void main(String[] args) {
        // 文件saas-wms-out-jv_logs.log
        File file = new File("d:/a.txt");
        // 文件名称
        String fileName = file.getName();
        // 文件唯一码
        String fileCode = getFileCode(file);
        // 文件总大小
        long length = file.length();
        RequestContext requestContext = new RequestContext();
        requestContext.setFileCode(fileCode);
        requestContext.setFileName(fileName);
        requestContext.setLength(length);

        if (length <= BLOCK_SIZE) {
            // 不分块
            sendFile(file, requestContext);
        } else {
            // 分块
            sendBlockFile(file, requestContext);
        }
    }

    private static String getFileContent(File file, long startIndex) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(startIndex);
            // raf.setLength(BLOCK_SIZE);
            byte[] bytes = new byte[1024 * 1024];
            int len = 0;
            StringBuilder content = new StringBuilder();
            while ((len = raf.read(bytes)) > 0) {
                content.append(new String(bytes, 0, len));
            }
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFileCode(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            return bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void sendFile(File file, RequestContext requestContext) {
        requestContext.setIsBlock(0);
        BlockContext blockContext = new BlockContext();
        blockContext.setNum(1);
        blockContext.setIndex(0L);
        blockContext.setSize(requestContext.getLength());
        blockContext.setContent(getFileContent(file, 0));
        requestContext.setBlockContextList(Arrays.asList(blockContext));
        SocketContext context = new SocketContext();
        context.setHost("127.0.0.1");
        context.setPort(8888);
        context.setRequestContext(requestContext);
        long startTime = System.currentTimeMillis();
        String result = ClientSocket.send(context);
        System.out.println("传输用时[" + (System.currentTimeMillis() - startTime) + "]毫秒\n" + result);
    }

    private static void sendBlockFile(File file, RequestContext requestContext) {
        requestContext.setIsBlock(1);
        long count = requestContext.getLength() / BLOCK_SIZE;
        if (requestContext.getLength() % BLOCK_SIZE > 0) {
            count++;
        }
        List<BlockContext> blockContextList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BlockContext blockContext = new BlockContext();
            blockContext.setNum(i + 1);
            blockContext.setIndex(BLOCK_SIZE * i);
            blockContext.setSize(BLOCK_SIZE);
            blockContextList.add(blockContext);
        }
        requestContext.setBlockContextList(blockContextList);
        SocketContext context = new SocketContext();
        context.setHost("127.0.0.1");
        context.setPort(8888);
        context.setRequestContext(requestContext);
        long startTime = System.currentTimeMillis();
        String result = ClientSocket.send(context);
        System.out.println("分块信息：" + result);

        ExecutorService executorService = Executors.newFixedThreadPool(blockContextList.size());
        for (BlockContext blockContext : blockContextList) {
            String content = getFileContent(file, blockContext.getIndex());
            blockContext.setContent(content);
            executorService.execute(new ClientThread(requestContext));
        }
        executorService.isTerminated();
        executorService.shutdown();
        System.out.println("分块传输用时[" + (System.currentTimeMillis() - startTime) + "]毫秒");
    }
}
