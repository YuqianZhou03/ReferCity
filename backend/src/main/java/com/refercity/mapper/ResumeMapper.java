package com.refercity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.refercity.entity.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {

    /**
     * 根据 email_prefix 查找用户 ID
     * 注意：这里返回 Long 以匹配 bigint 类型
     */
    @Select("SELECT id FROM users WHERE email_prefix = #{emailPrefix} LIMIT 1")
    Long findIdByEmailPrefix(String emailPrefix);
}