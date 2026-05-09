package com.zifang.ops.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ops.core.domain.entity.ImageBuildDO;
import java.util.List;

public interface IImageBuildService extends IService<ImageBuildDO> {
    IPage<ImageBuildDO> pageBuild(int pageNum, int pageSize, String imageName, String appName, String branch, String env, String status);
    List<ImageBuildDO> listBuild();
    ImageBuildDO getBuild(Long id);
    void createBuild(ImageBuildDO build);
}
