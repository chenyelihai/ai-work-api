package com.example.aiworkapi.model.chat;


import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class GenChat implements Serializable {
    /**
     * 分析目标
     */
    private String question;


    private static final long serialVersionUID = 1L;
}
