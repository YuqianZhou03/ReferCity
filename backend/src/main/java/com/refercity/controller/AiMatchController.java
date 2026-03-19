package com.refercity.controller;

import com.refercity.dto.MatchReport;
import com.refercity.dto.MatchRequest;
import com.refercity.service.AiMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiMatchController {

    @Autowired
    private AiMatchService aiMatchService;

    @PostMapping("/match")
    public ResponseEntity<?> getMatchReport(@RequestBody MatchRequest request) {
        try {
            // 调用 Service 获取最终生成的匹配报告
            MatchReport report = aiMatchService.generateMatchReport(request.getJobId(), request.getEmail(), request.getQuery());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("AI 分析出错：" + e.getMessage());
        }
    }
}