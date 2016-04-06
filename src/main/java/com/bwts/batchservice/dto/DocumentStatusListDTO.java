package com.bwts.batchservice.dto;


import java.util.List;

public class DocumentStatusListDTO {
    private int totalResults;
    private int itemsPerPage;
    private int startIndex;

    private List<DocumentStatusDTO> ublDataList;

    public int getTotalResults() {
        return totalResults;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public List<DocumentStatusDTO> getUblDataList() {
        return ublDataList;
    }
}
