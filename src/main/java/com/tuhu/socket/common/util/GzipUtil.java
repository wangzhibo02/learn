package com.tuhu.socket.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * Gzip解压缩
 * 
 * @author xiongyan
 * @date 2019/12/23
 */
@Slf4j
public class GzipUtil {

    /**
     * 压缩GZip
     * 
     * @param data  
     * @return  
     */
    public static byte[] gZip(byte[] data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(out);) {
            gzip.write(data);
            gzip.finish();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("压缩GZip失败", e);
            return null;
        }
    }

    /**
     * 解压GZip  
     * 
     * @param data  
     * @return  
     */
    public static byte[] unGZip(byte[] data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); ByteArrayInputStream in = new ByteArrayInputStream(data); GZIPInputStream gzip = new GZIPInputStream(in);) {
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = gzip.read(bytes, 0, bytes.length)) != -1) {
                out.write(bytes, 0, len);
            }
            out.flush();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("压缩GZip失败", e);
            return null;
        }
    }

}