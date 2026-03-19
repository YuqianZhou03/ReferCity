package com.refercity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.refercity.entity.JobDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface JobDetailMapper extends BaseMapper<JobDetail> {

    /**
     * 核心方法：通过 ID 关联查询主表 jobs 和详情表 job_details
     * 返回 Map 方便前端一次性接收所有字段
     */
    @Select("SELECT j.id, j.title, j.company, j.salary, " +
            "d.full_description AS fullDescription, " +
            "d.requirement, d.location " +
            "FROM jobs j " +
            "INNER JOIN job_details d ON j.id = d.id " +
            "WHERE j.id = #{id}")
    Map<String, Object> getMergedJobDetail(Integer id);
}