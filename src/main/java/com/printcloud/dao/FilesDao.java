package com.printcloud.dao;

import com.printcloud.model.File;
import org.springframework.data.repository.CrudRepository;

public interface FilesDao extends CrudRepository<File, String> {
}
