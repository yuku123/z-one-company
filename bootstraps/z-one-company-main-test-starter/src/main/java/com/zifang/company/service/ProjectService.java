package com.zifang.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.z.task.core.entity.Project;

import java.util.List;

/**
 * 项目表 Service
 *
 * @author zifang
 */
public interface ProjectService extends IService<Project> {

    /**
     * 创建项目
     *
     * @param project 项目信息
     * @param ownerId 所有者ID
     * @return 创建后的项目
     */
    Project createProject(Project project, String ownerId);

    /**
     * 获取用户的项目列表
     *
     * @param userId 用户ID
     * @return 项目列表
     */
    List<Project> getUserProjects(String userId);

    /**
     * 归档项目
     *
     * @param projectId 项目ID
     * @param userId 操作用户ID
     * @return 是否成功
     */
    boolean archiveProject(Long projectId, String userId);

    /**
     * 检查用户是否是项目成员
     *
     * @param projectId 项目ID
     * @param userId 用户ID
     * @return 是否是成员
     */
    boolean isProjectMember(Long projectId, String userId);
}
