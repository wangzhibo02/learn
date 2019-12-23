package com.tuhu.socket.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.tuhu.socket.common.constant.SocketConstant;
import com.tuhu.socket.dto.BlockDto;
import com.tuhu.socket.dto.FileDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
@Slf4j
public class FileUtil {

    /**
     * 获取文件块信息
     * 
     * @param file
     * @param index
     * @param size
     * @return
     */
    public static byte[] getFileData(File file, long index, long size) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MappedByteBuffer mbf = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, index, size);
            byte[] bytes = new byte[(int) size];
            mbf.get(bytes);
            mbf.clear();
            return bytes;
        } catch (Exception e) {
            log.error("获取文件块信息失败，文件名：{}，开始索引：{}，块大小：{}", file.getName(), index, size, e);
            return null;
        }
    }

    /**
     * 获取文件唯一码
     * 
     * @param file
     * @return
     */
    public static String getFileCode(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            long size = file.length();
            if (size > Integer.MAX_VALUE) {
                size = Integer.MAX_VALUE;
            }
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, size);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            return bi.toString(16);
        } catch (Exception e) {
            log.error("获取文件唯一码失败，文件名：{}", file.getName(), e);
            return null;
        }
    }

    /**
     * 写文件
     * 
     * @param fileDto
     * @param data
     * @throws Exception
     */
    public static void writerFile(FileDto fileDto, byte[] data) throws Exception {
        // 文件目录
        String dirPath = getDirPath(fileDto.getFileCode());
        // 文件后缀
        String suffix = fileDto.getFileName().substring(fileDto.getFileName().lastIndexOf("."));
        // 块信息
        BlockDto blockDto = fileDto.getBlockList().get(0);
        // 文件
        File file = new File(dirPath + File.separator + fileDto.getFileCode() + "_" + blockDto.getNum() + suffix);
        try (FileOutputStream fos = new FileOutputStream(file);) {
            fos.write(data);
            fos.flush();
        } catch (Exception e) {
            log.error("写入文件失败", e);
        }
    }

    /**
     * 文件目录地址
     * 
     * @param fileCode
     * @return
     */
    private static String getDirPath(String fileCode) {
        String dirPath = SocketConstant.BASE_PATH + File.separator + fileCode;
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        return dirPath;
    }

    /**
     * 块文件合并
     * 
     * @param fileDto
     * @throws Exception
     */
    public static void mergeFile(FileDto fileDto) throws Exception {
        // 文件目录
        String dirPath = getDirPath(fileDto.getFileCode());
        // 块文件
        File[] files = new File(dirPath).listFiles();
        if (files.length < fileDto.getBlockTotal()) {
            return;
        }
        // 排序
        List<File> fileList = Arrays.stream(files).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
        // 要合并文件
        File mergeFile = new File(dirPath + File.separator + fileDto.getFileName());
        FileOutputStream fos = new FileOutputStream(mergeFile);
        FileChannel outChannel = fos.getChannel();
        // 块文件合并到新文件
        for (File file : fileList) {
            FileInputStream fis = new FileInputStream(file);
            FileChannel inChannel = fis.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inChannel.close();
            fis.close();
        }
        // 删除块文件
        for (File file : fileList) {
            file.delete();
        }
        // 关闭资源
        outChannel.close();
        fos.flush();
        fos.close();
    }

    /**
     * 检查文件，完成续传
     * 
     * @param fileDto
     */
    public static void checkFile(FileDto fileDto) {
        // 文件目录
        String dirPath = SocketConstant.BASE_PATH + File.separator + fileDto.getFileCode();
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            return;
        }
        File[] fileList = dirFile.listFiles();
        if (fileList.length == 0) {
            return;
        }
        if (fileList.length == 1 && fileDto.getFileName().equals(dirFile.listFiles()[0].getName())) {
            fileDto.setBlockList(null);
        } else {
            List<Integer> nums = new ArrayList<>(fileList.length);
            for (File file : fileList) {
                String name = file.getName().split("\\.")[0];
                nums.add(Integer.valueOf(name.split("_")[1]));
            }
            for (Iterator<BlockDto> it = fileDto.getBlockList().iterator(); it.hasNext();) {
                BlockDto blockDto = it.next();
                if (nums.contains(blockDto.getNum())) {
                    it.remove();
                }
            }
        }
    }
}
