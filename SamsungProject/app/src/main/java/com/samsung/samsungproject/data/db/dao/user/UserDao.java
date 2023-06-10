package com.samsung.samsungproject.data.db.dao.user;

import com.samsung.samsungproject.domain.model.User;

import java.util.List;

public interface UserDao {

    long insert(User user);
    void insertAll(List<User> userList);
    List<User> findAll();
    User findById(long id);
    long update(long id, User user);
    void deleteAll();
}
