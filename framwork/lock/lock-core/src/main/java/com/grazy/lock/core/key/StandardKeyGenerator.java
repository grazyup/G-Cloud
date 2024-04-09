package com.grazy.lock.core.key;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.grazy.lock.core.context.LockContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-04-09 11:36
 * @Description: 标准的key生成器
 */

@Component
public class StandardKeyGenerator extends AbstractKeyGenerator {

    /**
     * 标准key的生成方法
     * 生成格式：className:methodName:parameterType1:...:value1:value2:...
     *
     * @param lockContext
     * @param keyValueMap
     * @return
     */
    @Override
    protected String doGenerateKey(LockContext lockContext, HashMap<String, String> keyValueMap) {
        List<String> keyList = Lists.newArrayList();
        keyList.add(lockContext.getClassName());
        keyList.add(lockContext.getMethodName());
        Class[] parameterTypes = lockContext.getParameterTypes();
        if(ArrayUtil.isNotEmpty(parameterTypes)){
            Arrays.stream(parameterTypes).forEach(parameterType -> keyList.add(parameterType.toString()));
        }else {
            keyList.add(void.class.toString());
        }
        Collection<String> values = keyValueMap.values();
        if(CollectionUtil.isNotEmpty(values)){
            keyList.addAll(values);
        }

//        return keyList.stream().collect(Collectors.joining(","));
        return Joiner.on(",").join(keyList);
    }
}
