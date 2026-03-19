package com.refercity.controller;

import com.refercity.entity.User;
import com.refercity.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.appSecret}")
    private String appSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 接口 1：静默登录检查
     * 修改点：确保 user 不为空时，一定返回 emailPrefix 供前端缓存
     */
    @PostMapping("/checkLogin")
    public Map<String, Object> checkLogin(@RequestBody Map<String, String> data) {
        String code = data.get("code");
        String openid = getOpenIdFromWechat(code);

        Map<String, Object> result = new HashMap<>();
        if (openid == null) {
            result.put("registered", false);
            result.put("msg", "微信认证失败");
            return result;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        User user = userMapper.selectOne(queryWrapper);

        boolean isRegistered = user != null;
        result.put("registered", isRegistered);

        if (isRegistered) {
            // 前端需要这个值做 wx.setStorageSync('emailPrefix', ...)
            result.put("emailPrefix", user.getEmailPrefix());
            result.put("msg", "登录成功");
        } else {
            result.put("msg", "用户未注册");
        }
        return result;
    }

    /**
     * 接口 2：正式注册
     * 修改点：注册成功后，把 emailPrefix 返回给前端，方便前端直接存入缓存并跳转
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> data) {
        String emailPrefix = data.get("emailPrefix");
        String code = data.get("code");
        String openid = getOpenIdFromWechat(code);

        Map<String, Object> result = new HashMap<>();

        if (openid == null) {
            result.put("code", 500);
            result.put("msg", "微信认证失败");
            return result;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        if (userMapper.selectOne(queryWrapper) != null) {
            result.put("code", 400);
            result.put("msg", "该微信已注册过，请直接登录");
            return result;
        }

        User user = new User();
        user.setOpenid(openid);
        user.setEmailPrefix(emailPrefix);
        user.setFullEmail(emailPrefix + "@my.cityu.edu.hk");
        userMapper.insert(user);

        result.put("code", 200);
        result.put("msg", "注册成功！");
        // 【新增】：注册成功后立即返回前缀，前端收到后直接存 Storage
        result.put("emailPrefix", emailPrefix);

        return result;
    }

    private String getOpenIdFromWechat(String code) {
        try {
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId +
                    "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code";

            String response = restTemplate.getForObject(url, String.class);
            Map<String, Object> wxMap = objectMapper.readValue(response, Map.class);
            return (String) wxMap.get("openid");
        } catch (Exception e) {
            System.err.println("请求微信接口异常: " + e.getMessage());
            return null;
        }
    }
}