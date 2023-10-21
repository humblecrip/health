package com.chenjiayan.dao;

import com.chenjiayan.pojo.Role;

import java.util.Set;

public interface RoleDao {
    Set<Role> findByUserId(Integer userId);
}
