package com.chenjiayan.service;

import com.chenjiayan.entitiy.PageResult;
import com.chenjiayan.entitiy.QueryPageBean;
import com.chenjiayan.pojo.CheckItem;

import java.util.List;

public interface CheckItemService {
    void add(CheckItem checkItem);
    PageResult pageQuery(QueryPageBean queryPageBean);

    void deleteById(Integer id);

    void edit(CheckItem checkItem);

    CheckItem findById(Integer id);

    List<CheckItem> findAll();

}
