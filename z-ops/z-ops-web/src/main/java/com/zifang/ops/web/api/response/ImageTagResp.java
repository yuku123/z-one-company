package com.zifang.ops.web.api.response;

import java.time.LocalDateTime;

public class ImageTagResp {
    private Long id;
    private Long imageId;
    private String tag;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
