package com.grazy.storage.engine.local.initializer;

import com.grazy.storage.engine.local.config.LocalStorageEngineConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * @Author: grazy
 * @Date: 2024-03-19 19:45
 * @Description: 初始化上传文件根目录与分片上传存储的根目录的初始化器（在项目启动是执行，创建对应的文件夹目录）
 */

@Component
@Slf4j
public class UploadFolderAndChunksFolderInitializer implements CommandLineRunner {

    @Resource
    private LocalStorageEngineConfigProperties configProperties;

    @Override
    public void run(String... args) throws Exception {
        FileUtils.forceMkdir(new File(configProperties.getRootFilePath()));
        log.info("the root file path has been created!");
        FileUtils.forceMkdir(new File(configProperties.getRootChunkFilePath()));
        log.info("the root file chunk path has been created!");
    }
}
