package com.zifang.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.task.core.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目表 Mapper
 *
 * @author zifang
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
