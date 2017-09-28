package com.printcloud.dao;

import com.printcloud.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UsersDao extends CrudRepository<User, String> {
    User findByUsername(String username);
}
