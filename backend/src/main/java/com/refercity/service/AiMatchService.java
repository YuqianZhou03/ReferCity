package com.refercity.service;

import com.refercity.dto.MatchReport;
import com.refercity.entity.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiMatchService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DeepSeekService deepSeekService;

    @Autowired
    private PythonRAGService pythonRAGService;

    public MatchReport generateMatchReport(Long jobId, String emailPrefix, String userQuery) throws Exception {

        // 【逻辑 A】查询职位详情
        String jobSql = "SELECT full_description, requirement, location FROM job_details WHERE id = ?";
        Map<String, Object> jobData = jdbcTemplate.queryForMap(jobSql, jobId);

        // 【逻辑 B】查询简历详情 (已按你的要求修正为 email_prefix)
        String resumeSql = "SELECT r.`name`, r.`edu`, r.`skills`, r.`experience` " +
                "FROM `resumes` r " +
                "JOIN `users` u ON r.`user_id` = u.`id` " +
                "WHERE u.`email_prefix` = ?";

        Resume resume = jdbcTemplate.queryForObject(
                resumeSql,
                new BeanPropertyRowMapper<>(Resume.class),
                emailPrefix
        );

        // --- 【 RAG 强化学习检索 】 ---
        // 调用你在 5000 端口启动的 Python FastAPI 服务
        // userQuery 是从前端传进来的那个包含“我想面试xx岗...”的长字符串
        List<String> retrievedAlumniTips = pythonRAGService.retrieveAlumniExperiences(userQuery);

        // 【逻辑 C】将两份真实数据喂给 DeepSeekService 进行分析
        MatchReport report = deepSeekService.matchResumeWithJob(jobData, resume, retrievedAlumniTips);

        // 【逻辑 D】补全代码：调用 Python 生成雷达图 Base64
        if (report.getRadarValues() != null && !report.getRadarValues().isEmpty()) {
            String base64Image = generateRadarBase64(report.getRadarValues());
            report.setRadarImageUrl(base64Image);
        }

        return report;
    }

    /**
     * 调用 Python 脚本并获取图片的 Base64 字符串
     */
    private String generateRadarBase64(List<Integer> values) {
        try {
            // 将 [80, 90, 70, 85, 60] 转为 "80,90,70,85,60"
            String valuesStr = values.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // 执行 Python 脚本 (确保 python 已在环境变量，且脚本在项目根目录的 scripts 文件夹下)
            ProcessBuilder pb = new ProcessBuilder("python", "scripts/generate_radar.py", valuesStr);
            Process process = pb.start();

            // 读取 Python 打印到控制台的 Base64 字符串
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String base64Output = reader.readLine();

            int exitCode = process.waitFor();
            if (exitCode == 0 && base64Output != null) {
                // 拼接前端 <img> 标签可以直接识别的 Data URI 前缀
                return "data:image/png;base64," + base64Output;
            } else {
                System.err.println("Python 绘图脚本执行失败，退出码: " + exitCode);
            }
        } catch (Exception e) {
            System.err.println("调用 Python 脚本异常: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // 失败则返回空，前端不显示图片
    }
}