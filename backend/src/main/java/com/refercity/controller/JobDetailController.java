package com.refercity.controller;

import com.refercity.mapper.JobDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/job-details")
@CrossOrigin
public class JobDetailController {

    @Autowired
    private JobDetailMapper jobDetailMapper;

    /**
     * 前端详情页调用：GET http://localhost:8080/api/job-details/1
     */
    @GetMapping("/{id}")
    public Map<String, Object> getDetail(@PathVariable Integer id) {
        // 直接调用合并查询
        return jobDetailMapper.getMergedJobDetail(id);
    }
}