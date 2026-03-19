package com.refercity.controller;

import com.refercity.entity.Resume;
import com.refercity.mapper.ResumeMapper;
import com.refercity.service.DeepSeekService;
import com.refercity.util.PDFUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin // 允许小程序跨域调用
public class ResumeController {

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private DeepSeekService deepSeekService; // 4. 注入 AI 服务

    /**
     * 新增：AI 自动解析上传的 PDF 简历
     * 流程：前端上传文件 -> 后端提词 -> AI 结构化 -> 返回给前端回填
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndParse(@RequestParam("file") MultipartFile file) {
        try {
            // A. 基础校验：检查文件是否为空
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("msg", "上传的文件为空"));
            }

            // B. 调用 PDFUtil 提取文本（“生肉”）
            String rawText = PDFUtil.extractText(file);

            // C. 调用 DeepSeekService 将文本转化为结构化的 Resume 对象
            // 这里 parsedResume 的字段已经根据你的实体类被强制约束了
            Resume parsedResume = deepSeekService.parseResumeText(rawText);

            // D. 仅返回解析结果，不直接存库，由用户核对后再点 save
            return ResponseEntity.ok(parsedResume);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("msg", "AI 解析失败：" + e.getMessage()));
        }
    }

    /**
     * 保存/更新简历信息
     * 对应前端的 saveResume 方法
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveResume(@RequestBody Map<String, String> data) {
        String emailPrefix = data.get("email");

        // 1. 获取对应的用户 ID
        Long userId = resumeMapper.findIdByEmailPrefix(emailPrefix);
        if (userId == null) {
            return ResponseEntity.status(404).body(Map.of("msg", "用户身份异常，请重新登录"));
        }

        // 2. 构造 Resume 对象
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setName(data.get("name"));
        resume.setEdu(data.get("edu"));
        resume.setSkills(data.get("skills"));
        resume.setExperience(data.get("experience"));

        // 3. 执行“保存或修改”逻辑
        if (resumeMapper.selectById(userId) != null) {
            resumeMapper.updateById(resume);
        } else {
            resumeMapper.insert(resume);
        }

        return ResponseEntity.ok(Map.of("success", true, "msg", "同步成功"));
    }



    /**
     * 查询简历状态及详情
     * 用于详情页卡片变色和编辑页数据回填
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        // 1. 获取用户 ID
        Long userId = resumeMapper.findIdByEmailPrefix(email);
        if (userId == null) {
            response.put("exists", false);
            return response;
        }

        // 2. 查找简历详情
        Resume resume = resumeMapper.selectById(userId);
        if (resume != null) {
            response.put("exists", true);
            response.put("resume", resume); // 返回整个对象，方便前端直接 setData
        } else {
            response.put("exists", false);
        }

        return response;
    }
}