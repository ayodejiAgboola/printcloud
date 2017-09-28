package com.printcloud.dao;

import com.printcloud.model.Job;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FilesDao extends CrudRepository<Job, String> {
    List<Job> findByStatus(String status);
    Job findByFileName(String filename);
}
