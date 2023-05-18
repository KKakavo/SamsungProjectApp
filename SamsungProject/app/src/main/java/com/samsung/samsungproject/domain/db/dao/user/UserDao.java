package com.samsung.samsungproject.domain.db.dao.user;

import com.samsung.samsungproject.domain.model.User;

import java.util.List;

public interface UserDao {

    long insert(User user);
    List<User> findAll();
    User findById(long id);
    long update(long id, User user);
    void deleteAll();
}
