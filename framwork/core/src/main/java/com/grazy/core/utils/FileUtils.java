package com.grazy.core.utils;

import cn.hutool.core.date.DateUtil;
import com.grazy.core.constants.GCloudConstants;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-02-29 21:06
 * @Description: 文件工具类
 */
public class FileUtils {

    /**
     * 根据文件名获取文件后缀
     */
    public static String getFileSuffix(String filename){
        if(Strings.isBlank(filename) || filename.indexOf(GCloudConstants.POINT_STR) == GCloudConstants.MINUS_ONE_STR){
            return GCloudConstants.EMPTY_STR;
        }
        return filename.substring(filename.indexOf(GCloudConstants.POINT_STR));
    }


    /**
     * 通过文件大小转化文件大小的展示名称
     *
     * @param totalSize
     * @return
     */
    public static String byteCountToDisplaySize(Long totalSize) {
        if(Objects.isNull(totalSize)){
            return GCloudConstants.EMPTY_STR;
        }
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(totalSize);
    }


    /**
     * 批量删除物理文件
     *
     * @param realFilePathList
     */
    public static void deleteFiles(List<String> realFilePathList) throws IOException {
        if(CollectionUtils.isEmpty(realFilePathList)){
            return;
        }
        for(String el: realFilePathList){
            org.apache.commons.io.FileUtils.forceDelete(new File(el));
        }
    }


    /**
     * 构建正式文件存储路径
     * 生成规则: 基础路径 + 年月日 + 随机的文件名称
     *
     * @param basePath
     * @param filename
     * @return
     */
    public static String generateStoreRealFilePath(String basePath, String filename) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(getFileSuffix(filename))
                .toString();
    }


    /**
     * 将文件的输入流写入文件中
     * 使用底层的sendfile零拷贝提高传输效率
     *
     * @param inputStream
     * @param totalSize
     * @param targetFile
     */
    public static void writeStreamToFile(InputStream inputStream, Long totalSize, File targetFile) throws IOException {
        createFile(targetFile);
        RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw");
        FileChannel outputChannel = randomAccessFile.getChannel();
        ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
        outputChannel.transferFrom(inputChannel, 0L, totalSize);
        inputChannel.close();
        outputChannel.close();
        inputStream.close();
        randomAccessFile.close();
    }


    /**
     * 创建文件 （包含父文件夹一起创建）
     *
     * @param targetFile
     */
    public static void createFile(File targetFile) throws IOException {
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        targetFile.createNewFile();
    }


    /**
     * 生成默认文件存储路径前缀
     * 生成规则： 当前登录用户的文件目录 + GCloud
     * @return
     */
    public static String generateDefaultStoreRealFilePath() {
        return new StringBuffer(System.getProperty("user.home"))
                .append(File.separator)
                .append("GCloud")
                .toString();
    }


    /**
     * 生成默认分片文件存储路径前缀
     * 生成规则： 当前登录用户的文件目录 + GCloud + chunks
     *
     * @return
     */
    public static String generateDefaultStoreRealChunkFilePath() {
        return new StringBuffer(System.getProperty("user.home"))
                .append(File.separator)
                .append("GCloud")
                .append(File.separator)
                .append("chunks")
                .toString();
    }


    /**
     * 构建完整的分片文件存储路径
     * 生成规则: 基础路径 + 年月日 + 文件的唯一标识 +随机的文件名称 +  __,__ + 文件分片的下标
     * @param basePath
     * @param identifier
     * @param chunkNumber
     * @return
     */
    public static String generateStoreRealChunkFilePath(String basePath, String identifier, Integer chunkNumber) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(identifier)
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(GCloudConstants.COMMON_SEPARATOR)
                .append(chunkNumber)
                .toString();
    }


    /**
     * 追加写文件
     *
     * @param target
     * @param source
     */
    public static void appendWrite(Path target, Path source) throws IOException{
        Files.write(target, Files.readAllBytes(source), StandardOpenOption.APPEND);
    }


    /**
     * 利用零拷贝技术读取文件内容并写入到文件的输出流中
     *
     * @param fileInputStream
     * @param outputStream
     * @param length
     */
    public static void writeFileToOutputStream(FileInputStream fileInputStream, OutputStream outputStream, long length) throws IOException{
        FileChannel fileChannel = fileInputStream.getChannel();
        WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
        fileChannel.transferTo(GCloudConstants.ZERO_LONG, length, writableByteChannel);
        outputStream.flush();
        fileInputStream.close();
        outputStream.close();
        fileChannel.close();
        writableByteChannel.close();
    }
}
