package com.grazy.storage.engine.oss;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.FileUtils;
import com.grazy.core.utils.UUIDUtil;
import com.grazy.storage.engine.core.AbstractStorageEngine;
import com.grazy.storage.engine.core.context.*;
import com.grazy.storage.engine.oss.config.OSSStorageEngineConfigProperties;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-03-01 15:00
 * @Description: OSS文件存储引擎实现类
 */

@Component
public class OSSStorageEngine extends AbstractStorageEngine {

    private static final Integer TEN_THOUSAND_INT = 10000;

    private static final String CACHE_KEY_TEMPLATE = "oss_cache_upload_id_%s_%s";

    private static final String IDENTIFIER_KEY = "identifier";

    private static final String UPLOAD_ID_KEY = "uploadId";

    private static final String USER_ID_KEY = "userId";

    private static final String PART_NUMBER_KEY = "partNumber";

    private static final String E_TAG_KEY = "eTag";

    private static final String PART_SIZE_KEY = "partSize";

    private static final String PART_CRC_KEY = "partCRC";

    @Resource
    private OSSStorageEngineConfigProperties configProperties;

    @Resource
    private OSSClient ossClient;

    /**
     * 单文件上传
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String realPath = getFilePath(FileUtils.getFileSuffix(context.getFilename()));
        ossClient.putObject(configProperties.getBucketName(),realPath,context.getInputStream());
        context.setRealPath(realPath);
    }



    @Override
    protected void doDelete(DeleteStorageFileContext context) throws IOException {

    }


    /**
     * 执行保存物理分片文件
     *  OSS文件分片上传的步骤：
     *      1.初始化文件分片上传，获取一个全局唯一的uploadId
     *      2.并发上传分片文件，每一个文件分片都需要携带有初始化返回的uploadId
     *      3.所有分片上传完成，触发文件分片合并的操作
     *
     *  存在难点:
     *      1.我们的分片上传是在一个多线程并发环境下运行的，我们的程序需要保证我们初始化分片上次的操作只有一个线程可以做
     *      2.我们所有的文件分片都需要携带一个全局的uploadId,该uploadId就需要存放一个线程的共享空间中
     *      3.我们需要保证每一个文件分片都能够单独去调用文件分片上传（就是每个分片都会保存分片上传需要的参数数据），而不是依赖于全局的uploadId
     *
     *  解决方案：
     *      1.加锁实现单线程，目前按照单体架构去考虑，使用JVM的锁去保证一个线程初始化文件分片上传，如果后续扩展成分布式架构，需要更换成分布式锁
     *      2.使用缓存,缓存分为本地缓存与分布式缓存（Redis）,此次使用Redis
     *      3.我们想要把每一文件的参数都能通过文件的url来获取,需要定义一种数据格式，支持我们添加附件数据，并可方便解析出来，我们的实现方案可参考
     *      网络请求的url格式：fileRealPath?paramKey=paramValue
     *
     *  具体实现逻辑：
     *      1.校验文件分片数不得大于10000
     *      2.获取缓存key
     *      3.通过缓存key获取初始化后的实体对象，获取全局的uploadId和ObjectName
     *      4.如果key获取的对象为空，直接初始化
     *      5.执行文件分片上传操作
     *      6.上传完成，将全局的参数封装成一个可识别的url,保存到上下文中，用于业务的落库操作
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doStoreChunkFile(StoreChunkFileContext context) throws IOException {
        if(context.getTotalChunks() > TEN_THOUSAND_INT){
            throw new GCloudBusinessException("分片数超过了限制，分片数不得大于： " + TEN_THOUSAND_INT);
        }
        String cacheKey = getCacheKey(context.getIdentifier(),context.getUserId());
        ChunkUploadEntity chunkUploadEntity = getCache().get(cacheKey, ChunkUploadEntity.class);
        if(Objects.isNull(chunkUploadEntity)){
            //第一次传递分片,初始化分片
            chunkUploadEntity = initChunkUpload(context.getFilename(),cacheKey);
        }
        UploadPartRequest request = new UploadPartRequest();
        request.setBucketName(configProperties.getBucketName());
        request.setUploadId(chunkUploadEntity.uploadId);
        request.setKey(chunkUploadEntity.objectName);
        request.setInputStream(context.getInputStream());
        request.setPartSize(context.getCurrentChunkSize());
        request.setPartNumber(context.getChunkNumber());

        //执行分片上传
        UploadPartResult result = ossClient.uploadPart(request);
        if (Objects.isNull(result)) {
            throw new GCloudBusinessException("文件分片上传失败");
        }
        PartETag partETag = result.getPartETag();

        //封装分片文件url(分片的各种参数，JSONObject类似Map集合)
        JSONObject param = new JSONObject();
        param.put(IDENTIFIER_KEY,context.getIdentifier());
        param.put(UPLOAD_ID_KEY,chunkUploadEntity.getUploadId());
        param.put(USER_ID_KEY,context.getUserId());
        param.put(PART_NUMBER_KEY,partETag.getPartNumber());
        param.put(E_TAG_KEY,partETag.getETag());
        param.put(PART_SIZE_KEY,partETag.getPartSize());
        param.put(PART_CRC_KEY,partETag.getPartCRC());
        String url = assembleUrl(chunkUploadEntity.getObjectName(),param);

        context.setRealPath(url);
    }


    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {

    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException{

    }



    /********************************************** private方法 **********************************************/

    /**
     * 获取对象的完整名称-文件路径（ObjectName、key、ObjectKey）
     * 年/月/日/UUID.fileSuffix
     *
     * @param fileSuffix
     * @return
     */
    private String getFilePath(String fileSuffix) {
        return new StringBuffer()
                .append(DateUtil.thisYear())
                .append(GCloudConstants.SLASH_STR)
                .append(DateUtil.thisMonth() + 1)
                .append(GCloudConstants.SLASH_STR)
                .append(DateUtil.thisDayOfMonth())
                .append(GCloudConstants.SLASH_STR)
                .append(UUIDUtil.getUUID())
                .append(fileSuffix)
                .toString();
    }


    /**
     * 该实体为文件分片上传持久化之后的全局信息载体
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class ChunkUploadEntity implements Serializable{

        private static final long serialVersionUID = -6048994987592236095L;

        /**
         * 文件分片上传唯一Id
         */
        private String uploadId;

        /**
         * 文件分片上传的对象名称
         */
        private String objectName;
    }


    /**
     * 拼装URL
     *
     * @param baseUrl
     * @param params
     * @return baseUrl?paramKey1=paramValue1&paramKey2=paramValue2
     */
    private String assembleUrl(String baseUrl, JSONObject params) {
        if (Objects.isNull(params) || params.isEmpty()) {
            return baseUrl;
        }
        StringBuffer urlStringBuffer = new StringBuffer(baseUrl);
        urlStringBuffer.append(GCloudConstants.QUESTION_MARK_STR);
        List<String> paramsList = Lists.newArrayList();
        StringBuffer urlParamsStringBuffer = new StringBuffer();
        params.entrySet().forEach(entry -> {
            urlParamsStringBuffer.setLength(GCloudConstants.ZERO_INT);
            urlParamsStringBuffer.append(entry.getKey());
            urlParamsStringBuffer.append(GCloudConstants.EQUALS_MARK_STR);
            urlParamsStringBuffer.append(entry.getValue());
            paramsList.add(urlParamsStringBuffer.toString());
        });
        return urlStringBuffer.append(Joiner.on(GCloudConstants.AND_MARK_STR).join(paramsList)).toString();
    }


    /**
     * 获取基础URL
     *
     * @param url
     * @return
     */
    private String getBaseUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return GCloudConstants.EMPTY_STR;
        }
        if (checkHaveParams(url)) {
            return url.split(getSplitMark(GCloudConstants.QUESTION_MARK_STR))[0];
        }
        return url;
    }


    /**
     * 获取截取字符串的关键标识
     * 由于java的字符串分割会按照正则去截取
     * 我们的URL会影响标识的识别，故添加左右中括号去分组
     *
     * @param mark
     * @return
     */
    private String getSplitMark(String mark) {
        return new StringBuffer(GCloudConstants.LEFT_BRACKET_STR)
                .append(mark)
                .append(GCloudConstants.RIGHT_BRACKET_STR)
                .toString();
    }


    /**
     * 分析URL参数
     *
     * @param url
     * @return
     */
    private JSONObject analysisUrlParams(String url) {
        JSONObject result = new JSONObject();
        if (!checkHaveParams(url)) {
            return result;
        }
        String paramsPart = url.split(getSplitMark(GCloudConstants.QUESTION_MARK_STR))[1];
        if (StringUtils.isNotBlank(paramsPart)) {
            List<String> paramPairList = Splitter.on(GCloudConstants.AND_MARK_STR).splitToList(paramsPart);
            paramPairList.stream().forEach(paramPair -> {
                String[] paramArr = paramPair.split(getSplitMark(GCloudConstants.EQUALS_MARK_STR));
                if (paramArr != null && paramArr.length == GCloudConstants.TWO_INT) {
                    result.put(paramArr[0], paramArr[1]);
                }
            });
        }
        return result;
    }


    /**
     * 检查是否是含有参数的URL
     *
     * @param url
     * @return
     */
    private boolean checkHaveParams(String url) {
        return StringUtils.isNotBlank(url) && url.indexOf(GCloudConstants.QUESTION_MARK_STR) != GCloudConstants.MINUS_ONE_INT;
    }


    /**
     * 初始化分片上传
     *
     * @param filename
     * @param cacheKey
     * @return
     */
    private ChunkUploadEntity initChunkUpload(String filename, String cacheKey) {
        //获取文件路径（objectName）
        String filePath = getFilePath(FileUtils.getFileSuffix(filename));
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(configProperties.getBucketName(),filePath);
        InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
        if (Objects.isNull(result)) {
            throw new GCloudBusinessException("文件分片上传初始化失败");
        }
        ChunkUploadEntity chunkUploadEntity = new ChunkUploadEntity();
        chunkUploadEntity.setUploadId(result.getUploadId());
        chunkUploadEntity.setObjectName(filePath);

        //存储到缓存中
        getCache().put(cacheKey,chunkUploadEntity);
        return chunkUploadEntity;
    }


    /**
     * 获取分片上传的缓存Key
     *
     * @param identifier
     * @param userId
     * @return
     */
    private String getCacheKey(String identifier, Long userId) {
        return String.format(CACHE_KEY_TEMPLATE,identifier,userId);
    }
}
