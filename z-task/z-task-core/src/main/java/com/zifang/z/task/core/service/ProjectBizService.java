package com.zifang.z.task.core.service;

import com.zifang.z.task.core.service.dto.ProjectDTO;
import java.util.List;

public interface ProjectBizService {
    List<ProjectDTO> listByUser(String userId);
    ProjectDTO create(ProjectDTO dto, String ownerId);
    boolean archive(Long projectId, String userId);
    boolean isMember(Long projectId, String userId);
}
