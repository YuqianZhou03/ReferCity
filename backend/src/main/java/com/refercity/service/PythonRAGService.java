package com.refercity.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.Data;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@Service
public class PythonRAGService {

    // 建议在配置类中定义 RestTemplate Bean，这里直接 new 也可以
    private final RestTemplate restTemplate = new RestTemplate();

    // Python FastAPI 运行的地址
    private final String PYTHON_URL = "http://localhost:5000/search";

    /**
     * 核心方法：发送 Query 到 Python 向量库，取回相关的校友面经原文
     */
    public List<String> retrieveAlumniExperiences(String userQuery) {
        try {
            // 1. 构造请求体 (对应 Python 端 SearchRequest 的数据结构)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", userQuery);
            requestBody.put("top_k", 3); // 默认取最相关的 3 条

            // 2. 发送 POST 请求
            // PythonResponse 是我们定义的内部类，用来解析 Python 返回的 JSON
            PythonResponse response = restTemplate.postForObject(PYTHON_URL, requestBody, PythonResponse.class);

            if (response != null && response.getDocuments() != null) {
                System.out.println("✅ RAG 检索成功，获取到 " + response.getDocuments().size() + " 条面经");
                return response.getDocuments();
            }
        } catch (Exception e) {
            System.err.println("❌ 调用 Python RAG 服务失败: " + e.getMessage());
        }

        // 兜底方案：如果检索失败，返回空列表，不影响后续 DeepSeek 的正常逻辑
        return Collections.emptyList();
    }

    /**
     * 内部 DTO 类，用于匹配 Python 返回的 JSON 格式: {"documents": [...]}
     */
    @Data
    private static class PythonResponse {
        private List<String> documents;
    }
}
