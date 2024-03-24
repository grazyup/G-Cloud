package com.grazy.modules.share.converter;

import com.grazy.modules.share.context.CreateShareUrlContext;
import com.grazy.modules.share.po.CreateShareUrlPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @Author: grazy
 * @Date: 2024-03-24 20:58
 * @Description: 文件分享参数对象类型转换
 */

@Mapper(componentModel = "spring")
public interface ShareConverter {

    @Mapping(target = "userId",expression = "java(com.grazy.common.utils.UserIdUtil.get())")
    CreateShareUrlContext createShareUrlPoToCreateShareUrlContext(CreateShareUrlPo createShareUrlPo);
}
