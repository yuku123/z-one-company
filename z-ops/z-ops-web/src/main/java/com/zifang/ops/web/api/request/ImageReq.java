package com.zifang.ops.web.api.request;

public class ImageReq {
    private Long id;
    private String name;
    private String registry;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegistry() { return registry; }
    public void setRegistry(String registry) { this.registry = registry; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
