package com.zifang.z.config.common.model.config;

public class ZConfigHistoryPageRequest {
    private Integer pageNum;
    private Integer pageSize;
    private String search;
    private Long namespace;
    private String group;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Long getNamespace() {
        return namespace;
    }

    public void setNamespace(Long namespace) {
        this.namespace = namespace;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
