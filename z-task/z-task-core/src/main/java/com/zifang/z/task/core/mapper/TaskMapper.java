package com.zifang.z.task.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.task.core.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    @Select("SELECT * FROM z_task WHERE list_id = #{listId} ORDER BY position ASC, id ASC")
    List<Task> selectByListId(@Param("listId") Long listId);

    @Select("SELECT * FROM z_task WHERE project_id = #{projectId} ORDER BY created_at DESC")
    List<Task> selectByProjectId(@Param("projectId") Long projectId);

    @Select("SELECT t.* FROM z_task t INNER JOIN z_task_assignee ta ON t.id = ta.task_id WHERE ta.user_id = #{userId} ORDER BY t.due_date ASC")
    List<Task> selectByAssignee(@Param("userId") String userId);

    @Update("UPDATE z_task SET list_id = #{listId}, position = #{position}, updated_at = NOW() WHERE id = #{taskId}")
    int updatePosition(@Param("taskId") Long taskId, @Param("listId") Long listId, @Param("position") String position);
}
