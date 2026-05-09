package com.zifang.ops.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ops.core.domain.entity.ImageDO;
import com.zifang.ops.core.domain.entity.ImageTagDO;
import com.zifang.ops.core.domain.mapper.ImageMapper;
import com.zifang.ops.core.domain.mapper.ImageTagMapper;
import com.zifang.ops.core.domain.service.IImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, ImageDO> implements IImageService {

    @Resource
    private ImageTagMapper imageTagMapper;

    @Override
    public IPage<ImageDO> pageImage(int pageNum, int pageSize, String name) {
        LambdaQueryWrapper<ImageDO> q = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) q.like(ImageDO::getName, name);
        q.orderByDesc(ImageDO::getCreatedAt);
        return this.page(new Page<>(pageNum, pageSize), q);
    }

    @Override
    public List<ImageDO> listImage() {
        return this.list();
    }

    @Override
    public ImageDO getImage(Long id) {
        return this.getById(id);
    }

    @Override
    public void createImage(ImageDO image) {
        image.setCreatedAt(LocalDateTime.now());
        this.save(image);
    }

    @Override
    public void updateImage(ImageDO image) {
        image.setUpdatedAt(LocalDateTime.now());
        this.updateById(image);
    }

    @Override
    public void deleteImage(Long id) {
        this.removeById(id);
    }

    @Override
    public List<ImageTagDO> getTags(Long imageId) {
        LambdaQueryWrapper<ImageTagDO> q = new LambdaQueryWrapper<>();
        q.eq(ImageTagDO::getImageId, imageId).orderByDesc(ImageTagDO::getCreatedAt);
        return imageTagMapper.selectList(q);
    }

    @Override
    @Transactional
    public void addTag(ImageTagDO tag) {
        tag.setCreatedAt(LocalDateTime.now());
        imageTagMapper.insert(tag);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        imageTagMapper.deleteById(id);
    }
}
