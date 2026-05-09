package com.zifang.ops.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ops.core.domain.entity.ImageBuildDO;
import com.zifang.ops.core.domain.mapper.ImageBuildMapper;
import com.zifang.ops.core.domain.service.IImageBuildService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImageBuildServiceImpl extends ServiceImpl<ImageBuildMapper, ImageBuildDO> implements IImageBuildService {

    @Override
    public IPage<ImageBuildDO> pageBuild(int pageNum, int pageSize, String imageName, String appName, String branch, String env, String status) {
        LambdaQueryWrapper<ImageBuildDO> q = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(imageName)) q.like(ImageBuildDO::getImageName, imageName);
        if (StringUtils.hasText(appName)) q.eq(ImageBuildDO::getAppName, appName);
        if (StringUtils.hasText(branch)) q.eq(ImageBuildDO::getBranch, branch);
        if (StringUtils.hasText(env)) q.eq(ImageBuildDO::getEnv, env);
        if (StringUtils.hasText(status)) q.eq(ImageBuildDO::getStatus, status);
        q.orderByDesc(ImageBuildDO::getCreatedAt);
        return this.page(new Page<>(pageNum, pageSize), q);
    }

    @Override
    public List<ImageBuildDO> listBuild() {
        return this.list();
    }

    @Override
    public ImageBuildDO getBuild(Long id) {
        return this.getById(id);
    }

    @Override
    public void createBuild(ImageBuildDO build) {
        build.setCreatedAt(LocalDateTime.now());
        this.save(build);
    }
}
