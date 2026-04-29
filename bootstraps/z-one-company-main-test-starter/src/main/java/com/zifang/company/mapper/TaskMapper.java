package com.zifang.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.task.core.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 任务表 Mapper
 *
 * @author zifang
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 查询列表下的所有任务
     *
     * @param listId 列表ID
     * @return 任务列表
     */
    @Select("SELECT * FROM z_task WHERE list_id = #{listId} ORDER BY position ASC, id ASC")
    List<Task> selectByListId(@Param("listId") Long listId);

    /**
     * 查询项目下的所有任务
     *
     * @param projectId 项目ID
     * @return 任务列表
     */
    @Select("SELECT * FROM z_task WHERE project_id = #{projectId} ORDER BY created_at DESC")
    List<Task> selectByProjectId(@Param("projectId") Long projectId);

    /**
     * 查询分配给用户的任务
     *
     * @param userId 用户ID
     * @return 任务列表
     */
    @Select("SELECT t.* FROM z_task t INNER JOIN z_task_assignee ta ON t.id = ta.task_id WHERE ta.user_id = #{userId} ORDER BY t.due_date ASC")
    List<Task> selectByAssignee(@Param("userId") String userId);

    /**
     * 更新任务位置
     *
     * @param taskId 任务ID
     * @param listId 列表ID
     * @param position 位置
     * @return 更新行数
     */
    @Update("UPDATE z_task SET list_id = #{listId}, position = #{position}, updated_at = NOW() WHERE id = #{taskId}")
    int updatePosition(@Param("taskId") Long taskId, @Param("listId") Long listId, @Param("position") String position);
}
