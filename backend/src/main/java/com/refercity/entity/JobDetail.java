package com.refercity.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("job_details")
public class JobDetail {
    @TableId
    private Integer id;             // 对应 jobs 表的 id
    private String fullDescription; // 详细职责
    private String requirement;     // 任职要求
    private String location;        // 工作地点
}