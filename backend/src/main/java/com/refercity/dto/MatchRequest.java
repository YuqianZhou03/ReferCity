package com.refercity.dto; // 建议路径

import lombok.Data;

/**
 * 专门用于接收前端 AI 匹配请求的 DTO
 */
@Data
public class MatchRequest {
    private Long jobId;  // 对应前端传来的 jobId
    private String email; // 对应前端传来的 emailPrefix
    private String query;// 前端发送的用于RAG的prompt
}