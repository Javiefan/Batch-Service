package com.bwts.batchservice.dto;


import java.util.List;

public class DocumentStatusListDTO {
    private int totalCount;
    private int pageSize;
    private int pageNum;
    private int totalPages;

    private List<DocumentStatusDTO> items;


    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }


    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setItems(List<DocumentStatusDTO> items) {
        this.items = items;
    }

    public List<DocumentStatusDTO> getItems() {
        return items;
    }

}
