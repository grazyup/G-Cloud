package com.grazy.modules.file.controller;

import com.google.common.base.Splitter;
import com.grazy.common.utils.UserIdUtil;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.response.R;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.file.constants.FileConstants;
import com.grazy.modules.file.context.QueryFileListContext;
import com.grazy.modules.file.enums.DelFlagEnum;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.file.vo.GCloudUserFileVO;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @GetMapping("/file")
    public R<List<GCloudUserFileVO>> list(@NotBlank(message = "父文件夹ID不能为空") @RequestParam(value = "parentId",required = true) String parentId,
                                          @RequestParam(value = "fileType",required = false,defaultValue = FileConstants.ALL_FILE_TYPE) String fileType){
        Long decryptParentId = -1L;
        List<Integer> fileTypeList = null;
        if(!FileConstants.NO_DETAIL_FOLDER.equals(parentId)){ //判断parentId是否为-1（-1表示不是具体文件夹）
            //解密父文件夹Id
            decryptParentId = IdUtil.decrypt(parentId);
        }
        if(!Objects.equals(FileConstants.ALL_FILE_TYPE,fileType)){ //判断fileType文件类型是否为-1(-1表示全部文件类型)
            fileTypeList = Splitter.on(GCloudConstants.COMMON_SEPARATOR).splitToList(fileType).stream().map(Integer::valueOf).collect(Collectors.toList());
        }
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setFileTypeArray(fileTypeList);
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setUserId(UserIdUtil.get());
        queryFileListContext.setParentId(decryptParentId);
        List<GCloudUserFileVO> result = userFileService.getFileList(queryFileListContext);
        return R.data(result);
    }

}
