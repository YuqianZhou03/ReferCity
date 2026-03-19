package com.refercity.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("jobs")
public class Job {
    @TableId
    private Integer id;
    private String title;
    private String company;
    private String salary;
    private String description;
}