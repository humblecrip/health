package com.chenjiayan.dao;

import com.chenjiayan.pojo.User;

public interface UserDao {
    User findByUsername(String username);
}
