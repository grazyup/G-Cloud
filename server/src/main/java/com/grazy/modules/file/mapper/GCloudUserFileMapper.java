package com.grazy.modules.file.mapper;

import com.grazy.modules.file.context.QueryFileListContext;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grazy.modules.file.vo.UserFileVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author gaofu
* @description 针对表【g_cloud_user_file(用户文件信息表)】的数据库操作Mapper
* @createDate 2024-01-24 17:42:44
* @Entity com.grazy.modules.file.domain.GCloudUserFile
*/
public interface GCloudUserFileMapper extends BaseMapper<GCloudUserFile> {

    /**
     * 查询用户的文件列表
     * @param queryFileListContext 查询文件列表上下文
     * @return 列表集合
     */
    List<UserFileVO> selectFileList(@Param("queryFileParam") QueryFileListContext queryFileListContext);
}




