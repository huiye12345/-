package com.community.util;

import java.util.List;

/**
 * 分页结果封装类
 * 这是一个独立的工具类，不影响现有功能
 * 用于统一封装分页查询结果
 */
public class PageResult<T> {
    
    /**
     * 当前页码
     */
    private Integer currentPage;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    /**
     * 当前页数据
     */
    private List<T> records;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;
    
    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;
    
    public PageResult() {}
    
    public PageResult(Integer currentPage, Integer pageSize, Long total, List<T> records) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;
        
        // 计算总页数
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        if (this.totalPages == 0) {
            this.totalPages = 1;
        }
        
        // 计算是否有上下页
        this.hasNext = currentPage < this.totalPages;
        this.hasPrevious = currentPage > 1;
    }
    
    // Getters and Setters
    public Integer getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public Integer getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    
    public List<T> getRecords() {
        return records;
    }
    
    public void setRecords(List<T> records) {
        this.records = records;
    }
    
    public Boolean getHasNext() {
        return hasNext;
    }
    
    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public Boolean getHasPrevious() {
        return hasPrevious;
    }
    
    public void setHasPrevious(Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
    
    /**
     * 创建空的分页结果
     */
    public static <T> PageResult<T> empty(Integer currentPage, Integer pageSize) {
        return new PageResult<>(currentPage, pageSize, 0L, null);
    }
}
