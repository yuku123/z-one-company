package com.zifang.z.task.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.task.core.entity.TaskAssignee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskAssigneeMapper extends BaseMapper<TaskAssignee> {

    @Select("SELECT user_id FROM z_task_task_assignee WHERE task_id = #{taskId}")
    List<String> selectUserIdsByTaskId(@Param("taskId") Long taskId);
}
