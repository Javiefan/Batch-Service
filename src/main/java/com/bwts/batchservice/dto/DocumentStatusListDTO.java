package com.bwts.batchservice.dto;


import java.util.List;

public class DocumentStatusListDTO {
    private int totalCount;
    private int pageSize;
    private int pageNum;
    private int totalPages;

    public int getTotalPages() {
        return totalPages;
    }

    private List<DocumentStatusDTO> items;

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }


    public List<DocumentStatusDTO> getItems() {
        return items;
    }
}
