package com.grazy.common.stream.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author: grazy
 * @Date: 2024-04-12 15:04
 * @Description: 声明事件传递通道
 *                  --> 配置文件加载完成后，channel通道就已经创建了，此处只是声明了通道的存在和它们的名称
 *                      （名称可以不用设置--注解的value值，但是不设置名称的情况下，方法名必须与配置文件中的配置的channel名称一致,这样才能匹配到;
 *                          若设置了名称，就根据设置的名称来匹配,方法名可以不用一致）
 */

public interface GCloudChannels {

    String TEST_INPUT = "testInput";

    String TEST_OUTPUT = "testOutput";


    String ERROR_LOG_INPUT = "errorLogInput";
    String ERROR_LOG_OUTPUT = "errorLogOutput";

    String DELETE_FILE_INPUT = "deleteFileInput";
    String DELETE_FILE_OUTPUT = "deleteFileOutput";

    String FILE_RESTORE_INPUT = "fileRestoreInput";
    String FILE_RESTORE_OUTPUT = "fileRestoreOutput";

    String PHYSICAL_DELETE_FILE_INPUT = "physicalDeleteFileInput";
    String PHYSICAL_DELETE_FILE_OUTPUT = "physicalDeleteFileOutput";

    String USER_SEARCH_INPUT = "userSearchInput";
    String USER_SEARCH_OUTPUT = "userSearchOutput";

    /**
     * 测试输入通道
     *
     * @return
     */
    @Input(TEST_INPUT)
    SubscribableChannel testInput();


    /**
     * 测试输出通道
     *
     * @return
     */
    @Output(TEST_OUTPUT)
    MessageChannel testOutput();


    @Input(GCloudChannels.ERROR_LOG_INPUT)
    SubscribableChannel errorLogInput();

    @Output(GCloudChannels.ERROR_LOG_OUTPUT)
    MessageChannel errorLogOutput();

    @Input(GCloudChannels.DELETE_FILE_INPUT)
    SubscribableChannel deleteFileInput();

    @Output(GCloudChannels.DELETE_FILE_OUTPUT)
    MessageChannel deleteFileOutput();

    @Input(GCloudChannels.FILE_RESTORE_INPUT)
    SubscribableChannel fileRestoreInput();

    @Output(GCloudChannels.FILE_RESTORE_OUTPUT)
    MessageChannel fileRestoreOutput();

    @Input(GCloudChannels.PHYSICAL_DELETE_FILE_INPUT)
    SubscribableChannel physicalDeleteFileInput();

    @Output(GCloudChannels.PHYSICAL_DELETE_FILE_OUTPUT)
    MessageChannel physicalDeleteFileOutput();

    @Input(GCloudChannels.USER_SEARCH_INPUT)
    SubscribableChannel userSearchInput();

    @Output(GCloudChannels.USER_SEARCH_OUTPUT)
    MessageChannel userSearchOutput();
}
