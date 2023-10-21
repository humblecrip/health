package com.chenjiayan.service;

import com.chenjiayan.entitiy.Result;

import java.util.Map;

public interface OrderService {
    Result order(Map map) throws Exception;

    Map findById(Integer id) throws Exception;
}
