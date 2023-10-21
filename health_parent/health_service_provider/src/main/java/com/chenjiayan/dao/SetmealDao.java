package com.chenjiayan.dao;


import com.chenjiayan.pojo.Setmeal;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SetmealDao {
    void add(Setmeal setmeal);

    void setSetmealAndCheckGroup(Map<String,Integer> map);


    Page<Setmeal> findByCondition(String queryString);

    List<Setmeal> getAll();

    Setmeal findSetMealById(Integer id);

    List<Map<String, Object>> findSetmealCount();
}
