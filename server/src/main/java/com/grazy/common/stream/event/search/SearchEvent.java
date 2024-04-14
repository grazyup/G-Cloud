package com.grazy.common.stream.event.search;

import lombok.*;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-12 17:47
 * @Description: 用户搜索事件
 */

@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class SearchEvent implements Serializable {

    private static final long serialVersionUID = -7337340464624716485L;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 当前登录的用户ID
     */
    private Long userId;


    public SearchEvent( String keyword, Long userId) {
        this.keyword = keyword;
        this.userId = userId;
    }
}
