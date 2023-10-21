package com.chenjiayan.service;

import com.chenjiayan.pojo.User;

public interface UserService {
    User findByUsername(String username);
}
