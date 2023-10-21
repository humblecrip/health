package com.chenjiayan.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.chenjiayan.entitiy.PageResult;
import com.chenjiayan.entitiy.QueryPageBean;
import com.chenjiayan.pojo.Setmeal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Service
public interface SetmealService {
    void add(Integer[] checkGroupIds,Setmeal setmeal);

    PageResult pageQuery(QueryPageBean queryPageBean);

    List<Setmeal> getAllSetmeal();

    Setmeal findById(Integer id);

    List<Map<String, Object>> findSetmealCount();
}
