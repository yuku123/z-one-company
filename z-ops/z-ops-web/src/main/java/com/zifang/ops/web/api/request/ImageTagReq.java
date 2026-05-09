package com.zifang.ops.web.api.request;

public class ImageTagReq {
    private Long imageId;
    private String tag;

    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}
