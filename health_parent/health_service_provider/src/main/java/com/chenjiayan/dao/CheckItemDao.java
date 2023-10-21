package com.chenjiayan.dao;

import com.chenjiayan.pojo.CheckItem;
import com.github.pagehelper.Page;

import java.util.List;


public interface CheckItemDao {
    void add(CheckItem checkItem);
    Page<CheckItem> selectByCondition(String queryString);
    long findCountByCheckItemId(Integer id);
    int deleteById(Integer id);
    void edit(CheckItem checkItem);
    CheckItem findById(Integer id);
    List<CheckItem> findAll();
}
