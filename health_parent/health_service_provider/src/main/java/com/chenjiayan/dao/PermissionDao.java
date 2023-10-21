package com.chenjiayan.dao;

import com.chenjiayan.pojo.Permission;

import java.util.Set;

public interface PermissionDao {
    Set<Permission> findByRoleId(Integer roleId);
}
