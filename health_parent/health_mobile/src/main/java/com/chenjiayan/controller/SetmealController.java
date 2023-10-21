package com.chenjiayan.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.chenjiayan.constant.MessageConstant;
import com.chenjiayan.entitiy.Result;
import com.chenjiayan.pojo.Setmeal;
import com.chenjiayan.service.SetmealService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Reference
    private SetmealService setmealService;

    @GetMapping("/getAllSetmeal")
    public Result getAllSetmeal(){
        try{
            List<Setmeal> setmealList = setmealService.getAllSetmeal();
            return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS,setmealList);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_LIST_FAIL);
        }
    }

    @PostMapping("/findById")
    public Result findById(Integer id){
        try{
            Setmeal setmeal = setmealService.findById(id);
            return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }
}
