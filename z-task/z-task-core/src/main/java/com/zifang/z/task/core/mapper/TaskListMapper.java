package com.zifang.z.task.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.task.core.entity.TaskList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskListMapper extends BaseMapper<TaskList> {

    @Select("SELECT * FROM z_task_task_list WHERE board_id = #{boardId} AND is_archived = 0 ORDER BY sort_order ASC, id ASC")
    List<TaskList> selectByBoardId(@Param("boardId") Long boardId);

    @Select("SELECT MAX(sort_order) FROM z_task_task_list WHERE board_id = #{boardId}")
    Integer selectMaxSortOrder(@Param("boardId") Long boardId);
}
