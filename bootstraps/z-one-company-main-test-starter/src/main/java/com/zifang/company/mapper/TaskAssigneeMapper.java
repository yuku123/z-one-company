package com.zifang.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.task.core.entity.TaskAssignee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务执行者关联表 Mapper
 *
 * @author zifang
 */
@Mapper
public interface TaskAssigneeMapper extends BaseMapper<TaskAssignee> {

    /**
     * 查询任务的执行者列表
     *
     * @param taskId 任务ID
     * @return 用户ID列表
     */
    @Select("SELECT user_id FROM z_task_assignee WHERE task_id = #{taskId}")
    List<String> selectUserIdsByTaskId(@Param("taskId") Long taskId);
}
