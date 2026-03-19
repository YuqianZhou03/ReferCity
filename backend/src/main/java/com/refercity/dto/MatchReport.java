package com.refercity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchReport {
    // 综合匹配百分比 (0-100)
    private int matchRate;

    // 雷达图的五个维度分值
    private List<Integer> radarValues;

    // AI 产出的深度文字分析
    private String analysis;

    // 针对性面试集锦建议列表
    private List<String> tips;

    // 后端 Python 生成的雷达图图片访问 URL
    private String radarImageUrl;
}
