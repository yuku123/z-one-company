package com.zifang.ops.web.api.request;

public class ImageBuildReq {
    private Long id;
    private String imageName;
    private String tag;
    private String appName;
    private String branch;
    private String env;
    private String status;
    private String imageTag;
    private String buildLog;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public String getEnv() { return env; }
    public void setEnv(String env) { this.env = env; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getImageTag() { return imageTag; }
    public void setImageTag(String imageTag) { this.imageTag = imageTag; }
    public String getBuildLog() { return buildLog; }
    public void setBuildLog(String buildLog) { this.buildLog = buildLog; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
