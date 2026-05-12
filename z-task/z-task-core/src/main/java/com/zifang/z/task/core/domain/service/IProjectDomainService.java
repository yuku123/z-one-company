package com.zifang.z.task.core.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.z.task.core.entity.Project;
import java.util.List;

public interface IProjectDomainService extends IService<Project> {
    Project createProject(Project project, String ownerId);
    List<Project> getUserProjects(String userId);
    boolean archiveProject(Long projectId, String userId);
    boolean isProjectMember(Long projectId, String userId);
}
