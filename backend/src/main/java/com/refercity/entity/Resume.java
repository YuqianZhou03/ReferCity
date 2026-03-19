package com.refercity.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

@Data
@TableName("resumes")
public class Resume {
    /**
     * 对应 users 表的 id (BIGINT)
     * 使用 IdType.INPUT 是因为我们的主键是由外部逻辑(用户ID)确定的，不是自增
     */
    @TableId(type = IdType.INPUT)
    private Long userId;

    private String name;
    private String edu;
    private String skills;
    private String experience;
}