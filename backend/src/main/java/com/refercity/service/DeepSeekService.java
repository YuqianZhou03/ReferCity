package com.refercity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.refercity.dto.MatchReport;
import com.refercity.entity.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeepSeekService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 升级版：引入检索到的真实面经 (tips) 进行增强分析
     */
    public MatchReport matchResumeWithJob(Map<String, Object> jobData, Resume resume, List<String> retrievedTips) throws Exception {

        // 1. 准备 System Prompt：强制要求 AI 必须引用面经中的真实考点
        String systemPrompt = "你是一位资深大厂面试官。我会给你【职位信息】、【候选人简历】以及【库中检索到的真实校友面经】。\n" +
                "你的任务：\n" +
                "1. 对比简历与职位的匹配度。\n" +
                "2. **核心要求**：必须参考提供的【校友面经】，分析面经中提到的高频考点（如特定场景题、算法原题）与候选人简历的差距。\n" +
                "3. 必须输出 JSON 格式，包含：'matchRate'(整数), 'radarValues'(5个整数), 'analysis'(包含面经考点的评价), 'tips'(基于面经的3条实战避坑建议)。\n" +
                "雷达图维度：[技术实力, 学历背景, 项目经验, 岗位匹配度, 发展潜力]。";

        // 2. 将面经列表转为结构化文本
        String tipsContent = retrievedTips.isEmpty() ? "暂无相关面经参考" :
                retrievedTips.stream().map(t -> "- " + t).collect(Collectors.joining("\n\n"));

        // 3. 准备 User Content：注入 RAG 检索结果
        String userContent = String.format(
                "### 【目标职位】\n职责与要求：%s %s\n\n" +
                        "### 【候选人简历】\n姓名：%s\n教育：%s\n技能：%s\n经历：%s\n\n" +
                        "### 【核心参考：该岗位校友真实面经】\n%s",
                jobData.get("full_description"),
                jobData.get("requirement"),
                resume.getName(),
                resume.getEdu(),
                resume.getSkills(),
                resume.getExperience(),
                tipsContent
        );

        // 4. 执行 AI 调用
        String jsonResponse = callDeepSeek(systemPrompt, userContent);

        // 5. 转换结果
        return objectMapper.readValue(jsonResponse, MatchReport.class);
    }

    /**
     * 通用调用方法 (保持不变)
     */
    private String callDeepSeek(String systemPrompt, String userContent) throws Exception {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(60000);
        RestTemplate restTemplate = new RestTemplate(factory);

        Map<String, Object> requestBody = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userContent)
                ),
                "response_format", Map.of("type", "json_object"),
                "temperature", 0.3
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
    }

    /**
     * 1. 简历解析逻辑（保持你之前的代码）
     */
    public Resume parseResumeText(String rawText) throws Exception {
        String systemPrompt = "你是一个简历解析助手。请将简历文本转化为 JSON 格式。" +
                "必须且只能包含以下键：'name', 'edu', 'skills', 'experience'。" +
                "注意：回答需要全中文，不要输出 Markdown 标签，只输出纯 JSON。";

        String jsonResponse = callDeepSeek(systemPrompt, rawText);
        return objectMapper.readValue(jsonResponse, Resume.class);
    }
}