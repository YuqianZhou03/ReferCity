package com.refercity.controller;

import com.refercity.entity.Job;
import com.refercity.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs") // 修改了路径前缀，更符合 RESTful 规范
@CrossOrigin
public class JobController {

    @Autowired
    private JobMapper jobMapper;

    /**
     * 获取所有校友内推职位列表
     * 访问路径：GET http://localhost:8080/api/jobs/list
     */
    @GetMapping("/list")
    public List<Job> getJobList() {
        // 使用 MyBatis-Plus 查询所有数据
        // 如果以后想按时间排序，可以改用：jobMapper.selectList(new QueryWrapper<Job>().orderByDesc("created_at"));
        return jobMapper.selectList(null);
    }
}