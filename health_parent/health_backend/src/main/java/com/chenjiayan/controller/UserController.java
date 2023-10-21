package com.chenjiayan.controller;

import com.chenjiayan.constant.MessageConstant;
import com.chenjiayan.entitiy.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户操作
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/getUsername")
    public Result getUsername(){
        // 当Spring security完成认证后，会将当前用户信息保存到框架提供的上下文对象
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user!=null){
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,user.getUsername());
        }
        return new Result(false, MessageConstant.GET_USERNAME_FAIL);
    }
}
