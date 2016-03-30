package com.bwts.batchservice.service;

import com.bwts.batchservice.dao.TaskDocLogDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class DocBatchService {

    private TaskDocLogDAO taskDocLogDAO;


    public DocBatchService(TaskDocLogDAO taskDocLogDAO) {
        this.taskDocLogDAO = taskDocLogDAO;
    }
}
