package com.chenjiayan.service;

import com.chenjiayan.entitiy.PageResult;
import com.chenjiayan.entitiy.QueryPageBean;
import com.chenjiayan.pojo.CheckGroup;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CheckGroupService {
    void add(Integer[]  checkitemIds, @RequestBody CheckGroup checkGroup);

    PageResult pageQuery(QueryPageBean queryPageBean);

    CheckGroup findById(Integer id);

    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    void edit(Integer[] checkitemIds, CheckGroup checkGroup);

    List<CheckGroup> findAll();

}
