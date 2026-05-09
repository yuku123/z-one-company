package com.zifang.ops.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ops.core.domain.entity.ImageDO;
import com.zifang.ops.core.domain.entity.ImageTagDO;
import java.util.List;

public interface IImageService extends IService<ImageDO> {
    IPage<ImageDO> pageImage(int pageNum, int pageSize, String name);
    List<ImageDO> listImage();
    ImageDO getImage(Long id);
    void createImage(ImageDO image);
    void updateImage(ImageDO image);
    void deleteImage(Long id);
    List<ImageTagDO> getTags(Long imageId);
    void addTag(ImageTagDO tag);
    void deleteTag(Long id);
}
