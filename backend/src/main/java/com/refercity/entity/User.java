package com.refercity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("users") // 对应数据库里的表名
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private String emailPrefix;
    private String fullEmail;
}