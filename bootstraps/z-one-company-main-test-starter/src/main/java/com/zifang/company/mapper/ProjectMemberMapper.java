package com.zifang.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.task.core.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 项目成员表 Mapper
 *
 * @author zifang
 */
@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {

    /**
     * 查询用户参与的所有项目ID
     *
     * @param userId 用户ID
     * @return 项目ID列表
     */
    @Select("SELECT project_id FROM z_project_member WHERE user_id = #{userId}")
    List<Long> selectProjectIdsByUserId(@Param("userId") String userId);

    /**
     * 查询项目成员
     *
     * @param projectId 项目ID
     * @param userId 用户ID
     * @return 成员信息
     */
    @Select("SELECT * FROM z_project_member WHERE project_id = #{projectId} AND user_id = #{userId}")
    ProjectMember selectByProjectAndUser(@Param("projectId") Long projectId, @Param("userId") String userId);
}
