package com.zifang.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.task.core.entity.TaskList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 列表表 Mapper
 *
 * @author zifang
 */
@Mapper
public interface TaskListMapper extends BaseMapper<TaskList> {

    /**
     * 查询看板下的所有列表
     *
     * @param boardId 看板ID
     * @return 列表列表
     */
    @Select("SELECT * FROM z_task_list WHERE board_id = #{boardId} AND is_archived = 0 ORDER BY sort_order ASC, id ASC")
    List<TaskList> selectByBoardId(@Param("boardId") Long boardId);

    /**
     * 查询列表的最大排序号
     *
     * @param boardId 看板ID
     * @return 最大排序号
     */
    @Select("SELECT MAX(sort_order) FROM z_task_list WHERE board_id = #{boardId}")
    Integer selectMaxSortOrder(@Param("boardId") Long boardId);
}
