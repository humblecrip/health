package com.chenjiayan.controller;

import com.chenjiayan.constant.MessageConstant;
import com.chenjiayan.constant.RedisMessageConstant;
import com.chenjiayan.entitiy.Result;
import com.chenjiayan.utils.SMSUtils;
import com.chenjiayan.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

/**
 * 验证码操作
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Autowired
    private JedisPool jedisPool;
    /**
     * 用户在线体检预约发送验证码
     * @param telephone
     * @return
     */
    @PostMapping("/send4Order")
    public Result send4Order(String telephone){
        // 随机生成4位数字验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(4);
        // 给用户发送验证码
        try {
            // SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode.toString());
            System.out.println(validateCode);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        // 将验证码保存到redis（5分钟）
        jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_ORDER,300,validateCode.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    /**
     * 用户手机快速登录发送验证码
     * @param telephone
     * @return
     */
    @PostMapping("/send4Login")
    public Result send4Login(String telephone){
        // 随机生成4位数字验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
        // 给用户发送验证码
        try {
            // SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode.toString());
            System.out.println(validateCode);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        // 将验证码保存到redis（5分钟）
        jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_LOGIN,300,validateCode.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
