package com.refercity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.refercity.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承了 BaseMapper 之后，你就拥有了插入、删除、查询等所有基本功能，不用自己写 SQL 了！
}