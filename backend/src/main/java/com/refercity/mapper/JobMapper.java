package com.refercity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.refercity.entity.Job;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobMapper extends BaseMapper<Job> {
}