package com.community.util;

/**
 * 分页查询参数封装类
 * 这是一个独立的工具类，不影响现有功能
 * 用于统一封装分页查询参数
 */
public class PageQuery {
    
    /**
     * 默认页码
     */
    private static final Integer DEFAULT_PAGE = 1;
    
    /**
     * 默认每页大小
     */
    private static final Integer DEFAULT_SIZE = 10;
    
    /**
     * 最大每页大小
     */
    private static final Integer MAX_SIZE = 100;
    
    /**
     * 当前页码（从1开始）
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 排序字段
     */
    private String sortField;
    
    /**
     * 排序方式（asc/desc）
     */
    private String sortOrder;
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    public PageQuery() {
        this.page = DEFAULT_PAGE;
        this.size = DEFAULT_SIZE;
    }
    
    /**
     * 获取当前页码
     */
    public Integer getPage() {
        if (page == null || page < 1) {
            return DEFAULT_PAGE;
        }
        return page;
    }
    
    /**
     * 获取每页大小
     */
    public Integer getSize() {
        if (size == null || size < 1) {
            return DEFAULT_SIZE;
        }
        if (size > MAX_SIZE) {
            return MAX_SIZE;
        }
        return size;
    }
    
    /**
     * 获取MyBatis-Plus的offset（从0开始）
     */
    public Long getOffset() {
        return (long) (getPage() - 1) * getSize();
    }
    
    /**
     * 获取MyBatis-Plus的limit
     */
    public Integer getLimit() {
        return getSize();
    }
    
    // Getters and Setters
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public String getSortField() {
        return sortField;
    }
    
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    /**
     * 是否是升序
     */
    public boolean isAsc() {
        return "asc".equalsIgnoreCase(sortOrder);
    }
    
    /**
     * 是否是降序
     */
    public boolean isDesc() {
        return sortOrder == null || "desc".equalsIgnoreCase(sortOrder);
    }
}
