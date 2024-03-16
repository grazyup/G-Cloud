package com.grazy.modules.file.controller;

import com.google.common.base.Splitter;
import com.grazy.common.utils.UserIdUtil;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.response.R;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.file.constants.FileConstants;
import com.grazy.modules.file.context.*;
import com.grazy.modules.file.converter.FileConverter;
import com.grazy.modules.file.enums.DelFlagEnum;
import com.grazy.modules.file.po.*;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.file.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2024-02-18 9:41
 * @Description: 文件模块控制层
 */

@Api(tags = "文件模块")
@RestController
@Validated
public class FileController {

    @Resource
    private GCloudUserFileService userFileService;

    @Resource
    private FileConverter fileConverter;

    @ApiOperation(
            value = "查询用户的文件列表",
            notes = "该接口提供了用户查询某文件夹下面的某些文件类型的文件列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("/files")
    public R<List<UserFileVO>> list(@NotBlank(message = "父文件夹ID不能为空") @RequestParam(value = "parentId",required = false) String parentId,
                                    @RequestParam(value = "fileType",required = false,defaultValue = FileConstants.ALL_FILE_TYPE) String fileType){
        Long decryptParentId = -1L;
        List<Integer> fileTypeList = null;
        if(!FileConstants.NO_DETAIL_FOLDER.equals(parentId)){ //判断parentId是否为-1（-1表示不是具体文件夹）
            //解密父文件夹Id
            decryptParentId = IdUtil.decrypt(parentId);
        }
        if(!Objects.equals(FileConstants.ALL_FILE_TYPE,fileType)){ //判断fileType文件类型是否为-1(-1表示全部文件类型)
            fileTypeList = Splitter.on(GCloudConstants.COMMON_SEPARATOR)
                    .splitToList(fileType)
                    .stream()
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setFileTypeArray(fileTypeList);
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setUserId(UserIdUtil.get());
        queryFileListContext.setParentId(decryptParentId);
        List<UserFileVO> result = userFileService.getFileList(queryFileListContext);
        return R.data(result);
    }


    @ApiOperation(
            value = "创建文件夹",
            notes = "该接口提供了用户创建文件夹的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("/file/folder")
    public R<String> createFolder(@Validated @RequestBody CreateFolderPo createFolderPo){
        CreateFolderContext createFolderContext = fileConverter.CreateFolderPoToCreateFolderContext(createFolderPo);
        Long folderId = userFileService.createFolder(createFolderContext);
        return R.data(IdUtil.encrypt(folderId));
    }


    @ApiOperation(
            value = "文件重命名",
            notes = "该接口提供了文件重命名的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PutMapping("/file")
    public R<String> updateFilename(@Validated @RequestBody UpdateFilenamePo updateFilenamePo){
        UpdateFilenameContext updateFilenameContext = fileConverter.UpdateFilenamePoToUpdateFilenameContext(updateFilenamePo);
        userFileService.updateFilename(updateFilenameContext);
        return R.success("SUCCESS");
    }


    @ApiOperation(
            value = "批量文件删除",
            notes = "该接口提供了批量文件删除的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @DeleteMapping("/file")
    public R<String> deleteFile(@Validated @RequestBody DeleteFilePo deleteFilePo){
        DeleteFileContext deleteFileContext = fileConverter.DeleteFilePoToDeleteFileContext(deleteFilePo);
        List<Long> fileIdList = Splitter.on(GCloudConstants.COMMON_SEPARATOR)
                .splitToList(deleteFilePo.getFileIds())
                .stream()
                .map(IdUtil::decrypt)
                .collect(Collectors.toList());
        deleteFileContext.setFileIdList(fileIdList);
        userFileService.deleteFile(deleteFileContext);
        return R.success("SUCCESS");
    }


    @ApiOperation(
            value = "文件秒传",
            notes = "该接口提供了文件秒传的功能",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("/file/sec-upload")
    public R<String> secUpload(@Validated @RequestBody SecUploadFilePo secUploadFilePo){
        SecUploadFileContext secUploadFileContext = fileConverter.SecUploadFilePoToSecUploadFileContext(secUploadFilePo);
        boolean result = userFileService.secUpload(secUploadFileContext);
        if(result){
            return R.success("SUCCESS");
        }
        return R.fail("文件唯一标识不存在，请手动执行文件上传");
    }


    @ApiOperation(
            value = "单文件上传",
            notes = "该接口提供了单文件上传的功能",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("/file/upload")
    @ApiImplicitParam(name = "Authorization", value = "Authorization",required = true, dataType = "String",paramType="header")
    public R<String> upload(@Validated FileUploadPo fileUploadPo){
        FileUploadContext fileUploadContext = fileConverter.FileUploadPoToFileUploadContext(fileUploadPo);
        userFileService.upload(fileUploadContext);
        return R.success();
    }


    @ApiOperation(
            value = "文件分片上传",
            notes = "该接口提供了文件分片上传的功能",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("/file/chunk-upload")
    public R<FileChunkUploadVO> chunkUpload(@Validated FileChunkUploadPo fileChunkUploadPo) {
        FileChunkUploadContext fileChunkUploadContext = fileConverter.FileChunkUploadPoToFileChunkUploadContext(fileChunkUploadPo);
        FileChunkUploadVO fileChunkUploadVO = userFileService.chunkUpload(fileChunkUploadContext);
        return R.data(fileChunkUploadVO);
    }


    @ApiOperation(
            value = "查询已经上传的文件分片列表-用于过滤掉已上传的分片",
            notes = "该接口提供了查询已经上传的文件分片列表的功能",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("/file/chunk-upload")
    public R<UploadChunksVo> getUploadChunks(@Validated QueryUploadChunkPo queryUploadChunkPo){
        QueryUploadChunkContext context = fileConverter.QueryUploadChunkPoToQueryUploadChunkContext(queryUploadChunkPo);
        UploadChunksVo uploadChunksVo = userFileService.getUploadedChunks(context);
        return R.data(uploadChunksVo);
    }


    @ApiOperation(
            value = "分片文件合并",
            notes = "该接口提供分片文件的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("/file/merge")
    public R<String> mergeFile(@Validated @RequestBody FileChunkMergePo fileChunkMergePo){
        FileChunkMergeContext fileChunkMergeContext = fileConverter.FileChunkMergePoToFileChunkMergeContext(fileChunkMergePo);
        userFileService.mergeFile(fileChunkMergeContext);
        return R.success();
    }


    @ApiOperation(
            value = "文件下载",
            notes = "该接口提供文件下载的功能，以二进制流的形式在response中输出",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE
    )
    @GetMapping("/file/download")
    public void download(@NotBlank(message = "文件ID不能为空")@RequestParam(value = "fileId",required = false) String fileId,
                         HttpServletResponse response){
        FileDownloadContext fileDownloadContext = new FileDownloadContext();
        fileDownloadContext.setFileId(fileId);
        fileDownloadContext.setResponse(response);
        fileDownloadContext.setUserId(UserIdUtil.get());
        userFileService.download(fileDownloadContext);
    }


    @ApiOperation(
            value = "文件预览",
            notes = "该接口提供文件预览的功能，以二进制流的形式在response中输出",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE
    )
    @GetMapping("/file/preview")
    public void preview(@NotBlank(message = "文件ID不能为空")@RequestParam(value = "fileId",required = false) String fileId,
                         HttpServletResponse response){
        FilePreviewContext filePreviewContext = new FilePreviewContext();
        filePreviewContext.setFileId(fileId);
        filePreviewContext.setResponse(response);
        filePreviewContext.setUserId(UserIdUtil.get());
        userFileService.preview(filePreviewContext);
    }


    @ApiOperation(
            value = "查询文件夹树",
            notes = "该接口提供查询文件夹树的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE
    )
    @GetMapping("/file/folder/tree")
    public R<List<FolderTreeNodeVo>> getFolderTree(){
        QueryFolderTreeContext context = new QueryFolderTreeContext();
        context.setUserId(UserIdUtil.get());
        List<FolderTreeNodeVo> folderTreeNodeVoList = userFileService.getFolderTree(context);
        return R.data(folderTreeNodeVoList);
    }


    @ApiOperation(
            value = "移动文件",
            notes = "该接口提供移动文件的功能",
            consumes = MediaType.APPLICATION_STREAM_JSON_VALUE,
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE
    )
    @PostMapping("/file/transfer")
    public R<String> transfer(@Validated @RequestBody TransferFilePo transferFilePo){
        TransferFileContext transferFileContext = new TransferFileContext();
        transferFileContext.setTargetParentId(IdUtil.decrypt(transferFilePo.getTargetParentId()));
        transferFileContext.setUserId(UserIdUtil.get());
        List<Long> fileIdList = Splitter.on(GCloudConstants.COMMON_SEPARATOR).
                splitToList(transferFilePo.getFileIds())
                .stream()
                .map(IdUtil::decrypt)
                .collect(Collectors.toList());
        transferFileContext.setFileIdList(fileIdList);
        userFileService.transfer(transferFileContext);
        return R.success("文件移动成功");
    }


    @ApiOperation(
            value = "文件复制",
            notes = "该接口提供文件复制的功能",
            consumes = MediaType.APPLICATION_STREAM_JSON_VALUE,
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE
    )
    @PostMapping("/file/copy")
    public R<String> copy(@Validated @RequestBody CopyFilePo copyFilePo){
        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setTargetParentId(IdUtil.decrypt(copyFilePo.getTargetParentId()));
        copyFileContext.setUserId(UserIdUtil.get());
        List<Long> fileIdList = Splitter.on(GCloudConstants.COMMON_SEPARATOR).
                splitToList(copyFilePo.getFileIds())
                .stream()
                .map(IdUtil::decrypt)
                .collect(Collectors.toList());
        copyFileContext.setFileIdList(fileIdList);
        userFileService.copy(copyFileContext);
        return R.success("文件复制成功");
    }


    @ApiOperation(
            value = "文件搜索",
            notes = "该接口提供了文件搜索的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("/file/search")
    public R<List<FileSearchResultVo>> search(@Validated FileSearchPo fileSearchPo){
        FileSearchContext fileSearchContext = new FileSearchContext();
        fileSearchContext.setUserId(UserIdUtil.get());
        fileSearchContext.setKeyword(fileSearchContext.getKeyword());
        String fileTypes = fileSearchPo.getFileTypes();
        if(StringUtils.isNotBlank(fileTypes) && !Objects.equals(FileConstants.NO_DETAIL_FOLDER,fileTypes)) {
            List<Integer> filetypeList = Splitter.on(GCloudConstants.COMMON_SEPARATOR)
                    .splitToList(fileTypes)
                    .stream()
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            fileSearchContext.setFileTypeList(filetypeList);
        }
        List<FileSearchResultVo> result = userFileService.search(fileSearchContext);
        return R.data(result);
    }


    @ApiOperation(
            value = "查询面包屑列表",
            notes = "该接口提供了查询面包屑列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("/file/breadcrumbs")
    public R<List<BreadCrumbsVo>> getBreadCrumbs(@NotBlank(message = "文件ID不能为空")
                                                     @RequestParam(value = "fileId",required = false) String fileId){
        QueryBreadCrumbsContext queryBreadCrumbsContext = new QueryBreadCrumbsContext();
        queryBreadCrumbsContext.setFileId(IdUtil.decrypt(fileId));  //是一个文件夹的文件ID
        queryBreadCrumbsContext.setUserId(UserIdUtil.get());
        List<BreadCrumbsVo> result = userFileService.getBreadCrumbs(queryBreadCrumbsContext);
        return R.data(result);
    }
}
